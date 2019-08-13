package demo;

import demo.data.IPv4Host;
import org.protelis.lang.datatype.DeviceUID;
import org.protelis.vm.CodePath;
import org.protelis.vm.NetworkManager;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.Channels;
import java.nio.channels.CompletionHandler;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implementation of a network manager which uses the socket TCP.
 */
public class SocketNetworkManager implements NetworkManager {

    private static final String DEFAULT_ADDRESS = "127.0.0.1";
    private transient Map<DeviceUID, Map<CodePath, Object>> messages = new HashMap<>();
    private final DeviceUID deviceUID;
    private final String address;
    private final int port;
    private final Set<IPv4Host> neighbors;
    private transient Thread t = null;

    /**
     * constructor method for device with default address.
     * @param deviceUID the device id
     * @param port port of the server for incoming message
     * @param neighbors the neighbors the device has to send his messages to
     */
    public SocketNetworkManager(final DeviceUID deviceUID, final int port, final Set<IPv4Host> neighbors) {
        this(deviceUID, DEFAULT_ADDRESS, port, neighbors);
    }

    /**
     * constructor method for device with default address.
     * @param deviceUID the device id
     * @param address address of the server for incoming message
     * @param port port of the server for incoming message
     * @param neighbors the neighbors the device has to send his messages to
     */
    public SocketNetworkManager(final DeviceUID deviceUID, final String address, final int port, final Set<IPv4Host> neighbors) {
        this.deviceUID = deviceUID;
        this.address = address;
        this.port = port;
        this.neighbors = neighbors;
    }

    /**
     * start the server TCP to listen for incoming messages from neighbors.
     * @throws IOException If some I/O error occurs
     */
    public void listen() throws IOException {
        AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open();
        server.bind(new InetSocketAddress(address, port));
        if (t == null) {
            t = new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    server.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {
                        @Override
                        public void completed(final AsynchronousSocketChannel clientChannel, final Object attachment) {
                            if (server.isOpen()) {
                                server.accept(null, this);
                            }
                            if (clientChannel != null && clientChannel.isOpen()) {
                                try {
                                    handleConnection(clientChannel);
                                } catch (IOException | ClassNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        @Override
                        public void failed(final Throwable exc, final Object attachment) {
                            exc.printStackTrace();
                        }
                    });
                }
                try {
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            t.start();
        }
    }

    /**
     * close the server.
     */
    public void stop() {
        if (t != null) {
            t.interrupt();
        }
    }

    private void handleConnection(final AsynchronousSocketChannel client) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(Channels.newInputStream(client));
        Object received = ois.readObject();
        ois.close();
        if (received instanceof Map) {
            ((Map) received).forEach((src, msg) -> receiveMessage((DeviceUID) src, (Map<CodePath, Object>) msg));
        }
    }

    private void receiveMessage(final DeviceUID src, final Map<CodePath, Object> msg) {
        messages.put(src, msg);
    }

    /**
     * Called by ProtelisVM read the stored messages.
     * @return the currently stored messages
     */
    @Override
    public Map<DeviceUID, Map<CodePath, Object>> getNeighborState() {
        Map<DeviceUID, Map<CodePath, Object>> t = messages;
        messages = new HashMap<>();
        return t;
    }

    /**
     * Called by ProtelisVM to send a message to the neighbors.
     * @param toSend the message to be sent.
     */
    @Override
    public void shareState(final Map<CodePath, Object> toSend) {
        Map<DeviceUID, Map<CodePath, Object>> msg = Stream.of(
                new AbstractMap.SimpleImmutableEntry<>(deviceUID, toSend)
        ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        neighbors.forEach(n -> {
            AsynchronousSocketChannel client = null;
            ObjectOutputStream oos = null;
            try {
                client = AsynchronousSocketChannel.open();
                InetSocketAddress hostAddress = new InetSocketAddress(n.getHost(), n.getPort());
                Future<Void> future = client.connect(hostAddress);
                future.get();
                oos = new ObjectOutputStream(Channels.newOutputStream(client));
                oos.writeObject(msg);
            } catch (IOException | InterruptedException | ExecutionException e) {
                e.printStackTrace();
            } finally {
                if (oos != null) {
                    try {
                        oos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (client != null) {
                    try {
                        client.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * Getter for the device id.
     * @return the device id
     */
    public DeviceUID getDeviceUID() {
        return deviceUID;
    }

    /**
     * Getter for the server address.
     * @return the server address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Getter for the server port.
     * @return the server port
     */
    public int getPort() {
        return port;
    }

    /**
     * Getter for the neighbors.
     * @return the neighbors
     */
    public Set<IPv4Host> getNeighbors() {
        return Collections.unmodifiableSet(neighbors);
    }
}
