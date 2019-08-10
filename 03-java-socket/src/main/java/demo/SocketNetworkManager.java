package demo;

import demo.data.IPv4Host;
import org.protelis.lang.datatype.DeviceUID;
import org.protelis.vm.CodePath;
import org.protelis.vm.NetworkManager;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Network manager which uses sockets to send and receive messages.
 */
public class SocketNetworkManager implements NetworkManager {
    private Map<DeviceUID, Map<CodePath, Object>> messages = new HashMap<>();
    private int timeout = 1000;
    private final DeviceUID uid;
    private final int port;
    private final Set<IPv4Host> neighbors;
    private Thread t = null;

    /**
     * Constructor method.
     * @param uid the id of the device
     * @param port the port the device exposes
     * @param neighbors the host and port of the neighbors
     */
    public SocketNetworkManager(final DeviceUID uid, final int port, final Set<IPv4Host> neighbors) {
        this.uid = uid;
        this.port = port;
        this.neighbors = neighbors;
    }

    /**
     * Makes the network manager able to receive messages.
     * @throws IOException if the port is already in use
     */
    public void listen() throws IOException {
        if (t == null || t.isInterrupted()) {
            ServerSocket server = new ServerSocket(port);
            server.setSoTimeout(timeout);
            t = new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        handleConnection(server.accept());
                    } catch (SocketTimeoutException e) {
                        //TODO: find a way to do this better
                    } catch (ClassNotFoundException | IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            t.start();
        }
    }

    /**
     * Stops the network manager from receiving messages.
     */
    public void stop() {
        if (t != null) {
            t.interrupt();
        }
    }

    private void handleConnection(final Socket client) throws IOException, ClassNotFoundException {
        Object received = new ObjectInputStream(client.getInputStream()).readObject();
        if (received instanceof Map) {
            ((Map) received).forEach((src, msg) -> {
                receiveMessage((DeviceUID) src, (Map<CodePath, Object>) msg);
            });
        }
    }

    private void receiveMessage(final DeviceUID src, final Map<CodePath, Object> msg) {
        messages.put(src, msg);
    }

    /**
     * Called by ProtelisVM.
     * @return the currently stored messages.
     */
    @Override
    public Map<DeviceUID, Map<CodePath, Object>> getNeighborState() {
        Map<DeviceUID, Map<CodePath, Object>> t = messages;
        messages = new HashMap<>();
        return t;
    }

    /**
     * Called by ProtelisVM.
     * Creates a new socket and sends a message to every neighbor.
     * @param toSend the message to be sent.
     */
    @Override
    public void shareState(final Map<CodePath, Object> toSend) {
        neighbors.forEach(n -> {
            try {
                Socket socket = new Socket(n.getHost(), n.getPort());
                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                Map<DeviceUID, Map<CodePath, Object>> msg = Stream.of(
                        new AbstractMap.SimpleImmutableEntry<>(uid, toSend)
                ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                outputStream.writeObject(msg);
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
