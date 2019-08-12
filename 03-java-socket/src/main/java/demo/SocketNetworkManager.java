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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SocketNetworkManager implements NetworkManager {

    private static final String DEFAULT_ADDRESS = "127.0.0.1";
    private Map<DeviceUID, Map<CodePath, Object>> messages = new HashMap<>();
    private final DeviceUID uid;
    private final String address;
    private final int port;
    private final Set<IPv4Host> neighbors;
    private Thread t = null;

    public SocketNetworkManager(final DeviceUID uid, final int port, final Set<IPv4Host> neighbors) {
        this(uid, DEFAULT_ADDRESS, port, neighbors);
    }

    public SocketNetworkManager(DeviceUID uid, String address, int port, Set<IPv4Host> neighbors) {
        this.uid = uid;
        this.address = address;
        this.port = port;
        this.neighbors = neighbors;
    }

    public void listen() throws IOException {
        AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open();
        server.bind(new InetSocketAddress(address, port));

        if (t == null || t.isInterrupted()) {
            t = new Thread(() -> {
                while(!Thread.currentThread().isInterrupted()) {
                    server.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {
                        @Override
                        public void completed(AsynchronousSocketChannel clientChannel, Object attachment) {
                            if (server.isOpen()){
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
                        public void failed(Throwable exc, Object attachment) {
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

    public void stop() {
        if (t != null) {
            t.interrupt();
        }
    }

    private void handleConnection(AsynchronousSocketChannel client) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(Channels.newInputStream(client));
        Object received = ois.readObject();
        ois.close();
        if (received instanceof Map) {
            ((Map) received).forEach((src, msg) -> receiveMessage((DeviceUID) src, (Map<CodePath, Object>) msg));
        }
    }

    private void receiveMessage(DeviceUID src, Map<CodePath, Object> msg) {
        messages.put(src, msg);
    }

    @Override
    public Map<DeviceUID, Map<CodePath, Object>> getNeighborState() {
        Map<DeviceUID, Map<CodePath, Object>> t = messages;
        messages = new HashMap<>();
        return t;
    }

    @Override
    public void shareState(Map<CodePath, Object> toSend) {
        Map<DeviceUID, Map<CodePath, Object>> msg = Stream.of(
                new AbstractMap.SimpleImmutableEntry<>(uid, toSend)
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
}
