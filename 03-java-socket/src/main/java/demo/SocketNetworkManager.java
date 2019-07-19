package demo;

import demo.data.IPv4Host;
import org.protelis.lang.datatype.DeviceUID;
import org.protelis.vm.CodePath;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SocketNetworkManager implements MyNetworkManager {

    private Map<DeviceUID, Map<CodePath, Object>> messages = new HashMap<>();
    private Map<CodePath, Object> toBeSent = Collections.emptyMap();
    private int timeout = 1000;
    private final DeviceUID uid;
    private final int port;
    private final Set<IPv4Host> neighbors;
    private boolean running = false;
    private Thread t = null;


    public SocketNetworkManager(final DeviceUID uid, final int port, final Set<IPv4Host> neighbors) {
        this.uid = uid;
        this.port = port;
        this.neighbors = neighbors;
    }

    public void listen() throws IOException {
        running = true;
        ServerSocket server = new ServerSocket(port);
        server.setSoTimeout(timeout);
        t = new Thread(() -> {
            while(!Thread.currentThread().isInterrupted()) {
                try {
                    handleConnection(server.accept());
                } catch (SocketTimeoutException e) {
                } catch (ClassNotFoundException e) {
                } catch (IOException e) {}
            }
        });
        t.start();
    }

    public void stop() {
        if (t != null) {
            t.interrupt();
        }
    }

    private void handleConnection(Socket client) throws IOException, ClassNotFoundException {
        Object received = new ObjectInputStream(client.getInputStream()).readObject();
        if (received instanceof Map) {
            ((Map) received).forEach((src, msg) -> {
                receiveMessage((DeviceUID) src, (Map<CodePath, Object>) msg);
            });
        }
    }

    private void receiveMessage(DeviceUID src, Map<CodePath, Object> msg) {
        messages.put(src, msg);
    }

    @Override
    public void sendMessages() {
        neighbors.forEach(n -> {
            try {
                Socket socket = new Socket(n.getHost(), n.getPort());
                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                Map<DeviceUID, Map<CodePath, Object>> msg = Stream.of(
                        new AbstractMap.SimpleImmutableEntry<>(uid, toBeSent)
                ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                outputStream.writeObject(msg);
                socket.close();
            } catch(IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public Map<DeviceUID, Map<CodePath, Object>> getNeighborState() {
        Map<DeviceUID, Map<CodePath, Object>> t = messages;
        messages = new HashMap<>();
        return t;
    }

    @Override
    public void shareState(Map<CodePath, Object> toSend) {
        toBeSent = toSend;
    }
}
