package org.protelis.demo;

import com.google.common.collect.ImmutableSet;
import org.protelis.demo.data.IPv4Host;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.protelis.lang.datatype.DeviceUID;
import org.protelis.vm.CodePath;
import org.protelis.vm.NetworkManager;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.Channels;
import java.nio.channels.CompletionHandler;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implementation of a network manager which uses the socket TCP.
 */
public class SocketNetworkManager implements NetworkManager {

    private static final InetAddress DEFAULT_ADDRESS = InetAddress.getLoopbackAddress();
    private transient Map<DeviceUID, Map<CodePath, Object>> messages = new HashMap<>();
    private final DeviceUID deviceUID;
    private final InetAddress address;
    private final int port;
    private final ImmutableSet<IPv4Host> neighbors;
    private transient Thread t;

    /**
     * constructor method for device with default address.
     * @param deviceUID the device id
     * @param port port of the server for incoming message
     * @param neighbors the neighbors the device has to send his messages to
     *
     */
    public SocketNetworkManager(
        final DeviceUID deviceUID,
        final int port,
        final Set<IPv4Host> neighbors
    ) {
        this(deviceUID, DEFAULT_ADDRESS, port, neighbors);
    }

    /**
     * constructor method for device with default address.
     * @param deviceUID the device id
     * @param address address of the server for incoming message
     * @param port port of the server for incoming message
     * @param neighbors the neighbors the device has to send his messages to
     */
    @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "The address is not mutable")
    public SocketNetworkManager(
            final DeviceUID deviceUID,
            final InetAddress address,
            final int port,
            final Set<IPv4Host> neighbors
    ) {
        this.deviceUID = deviceUID;
        this.address = address;
        this.port = port;
        this.neighbors = ImmutableSet.copyOf(neighbors);
    }

    /**
     * constructor method for device with default address.
     * @param deviceUID the device id
     * @param address address of the server for incoming message
     * @param port port of the server for incoming message
     * @param neighbors the neighbors the device has to send his messages to
     * @throws UnknownHostException if the host is unresolvable.
     */
    public SocketNetworkManager(
            final DeviceUID deviceUID,
            final String address,
            final int port,
            final Set<IPv4Host> neighbors
    ) throws UnknownHostException {
        this.deviceUID = deviceUID;
        this.address = InetAddress.getByName(address);
        this.port = port;
        this.neighbors = ImmutableSet.copyOf(neighbors);
    }

    /**
     * start the server TCP to listen for incoming messages from neighbors.
     * @throws IOException If some I/O error occurs
     */
    public void listen() throws IOException {
        final AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open(); // NOPMD
        server.bind(new InetSocketAddress(address, port));
        if (t == null) {
            t = new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    server.accept(null, new CompletionHandler<>() {
                        @Override
                        public void completed(final AsynchronousSocketChannel clientChannel, final Object attachment) {
                            if (server.isOpen()) {
                                server.accept(null, this);
                            }
                            if (clientChannel != null && clientChannel.isOpen()) {
                                try {
                                    handleConnection(clientChannel);
                                } catch (IOException | ClassNotFoundException e) {
                                    throw new IllegalStateException(e);
                                }
                            }
                        }

                        @Override
                        public void failed(final Throwable exc, final Object attachment) {
                            throw new IllegalStateException(attachment.toString(), exc);
                        }
                    });
                }
                try {
                    server.close();
                } catch (IOException e) {
                    throw new IllegalStateException(e);
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

    @SuppressWarnings("unchecked")
    private void handleConnection(final AsynchronousSocketChannel client) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(Channels.newInputStream(client))) {
            final Object received = ois.readObject();
            if (received instanceof Map) {
                ((Map<?, ?>) received).forEach((src, msg) ->
                        receiveMessage((DeviceUID) src, (Map<CodePath, Object>) msg)
                );
            }
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
        final Map<DeviceUID, Map<CodePath, Object>> t = messages;
        messages = new HashMap<>();
        return t;
    }

    /**
     * Called by ProtelisVM to send a message to the neighbors.
     * @param toSend the message to be sent.
     */
    @Override
    public void shareState(final Map<CodePath, Object> toSend) {
        final Map<DeviceUID, Map<CodePath, Object>> msg = Stream.of(
                new AbstractMap.SimpleImmutableEntry<>(deviceUID, toSend)
        ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        neighbors.forEach(n -> {
            try (AsynchronousSocketChannel client = AsynchronousSocketChannel.open()) {
                final InetSocketAddress hostAddress = new InetSocketAddress(n.getHost(), n.getPort());
                client.connect(hostAddress).get(10, TimeUnit.SECONDS);
                try (ObjectOutputStream oos = new ObjectOutputStream(Channels.newOutputStream(client))) {
                    oos.writeObject(msg);
                } catch (IOException e) {
                    throw new IllegalStateException(e);
                }
            } catch (IOException | InterruptedException | ExecutionException | TimeoutException e) {
                throw new IllegalStateException(e);
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
    @SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "The field is immutable")
    public Set<IPv4Host> getNeighbors() {
        return neighbors;
    }
}
