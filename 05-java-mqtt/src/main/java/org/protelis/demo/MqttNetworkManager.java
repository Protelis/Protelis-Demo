package org.protelis.demo;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.protelis.lang.datatype.DeviceUID;
import org.protelis.vm.CodePath;
import org.protelis.vm.NetworkManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Network Manager, which uses the MQTT protocol.
 */
public class MqttNetworkManager implements NetworkManager {

    private static final InetAddress DEFAULT_ADDRESS = InetAddress.getLoopbackAddress();
    private static final int DEFAULT_PORT = 1883;
    private static final int DEFAULT_QOS = 2;
    private final DeviceUID deviceUID;
    private final InetAddress address;
    private final int port;
    private final String clientId;
    private final List<String> neighbors;
    private final int qos;
    private IMqttAsyncClient mqttClient;
    private transient Map<DeviceUID, Map<CodePath, Object>> messages = new HashMap<>();

    /**
     * Constructor method for which uses the default config.
     *
     * @param deviceUID the device id.
     * @param neighbors the node neighbors.
     */
    public MqttNetworkManager(final DeviceUID deviceUID, final List<String> neighbors) {
        this(deviceUID, DEFAULT_ADDRESS, DEFAULT_PORT, neighbors);
    }

    /**
     * Constructor method to explicitly specify the broker address and port.
     *
     * @param deviceUID the device id.
     * @param address the MQTT broker address.
     * @param port the MQTT broker port.
     * @param neighbors the node neighbors.
     */
    public MqttNetworkManager(
        final DeviceUID deviceUID,
        final InetAddress address,
        final int port,
        final List<String> neighbors
    ) {
        this(deviceUID, address, port, DEFAULT_QOS, neighbors);
    }

    /**
     * Constructor method to explicitly set the MQTT qos.
     *
     * @param deviceUID the device id.
     * @param address the MQTT broker address.
     * @param port the MQTT broker port.
     * @param qos the MQTT qos parameter.
     * @param neighbors the node neighbors.
     */
    @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "The address is not mutable")
    public MqttNetworkManager(
        final DeviceUID deviceUID,
        final InetAddress address,
        final int port,
        final int qos,
        final List<String> neighbors
    ) {
        this.deviceUID = deviceUID;
        this.address = address;
        this.port = port;
        this.clientId = Integer.toString(deviceUID.hashCode());
        this.qos = qos;
        this.neighbors = new ArrayList<>(neighbors);
    }

    /**
     * Starts the MQTT client and subscribes to the target topic.
     *
     * @param topic the topic the node is listening to.
     * @return the token to track the listen asynchronous operation.
     * @throws MqttException if some MQTT-related error occurs.
     */
    public IMqttToken listen(final String topic) throws MqttException {
        final String broker = "tcp://" + this.address.getHostAddress() + ":" + this.port;
        final MqttClientPersistence persistence = new MemoryPersistence(); // NOPMD
        this.mqttClient = new MqttAsyncClient(broker, this.clientId, persistence);
        mqttClient.connect().waitForCompletion();
        return mqttClient.subscribe(topic, this.qos, null, null, (unused, message) -> handleMessage(message));
    }

    /**
     * Shutdowns the MQTT client.
     *
     * @return the token to track the asynchronous task
     * @throws MqttException if some MQTT-related error occurs
     */
    public IMqttToken stop() throws MqttException {
        return this.mqttClient.disconnect();
    }

    @SuppressWarnings("unchecked")
    private void handleMessage(final MqttMessage message) throws IOException, ClassNotFoundException {
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(message.getPayload()))) {
            final Object received = objectInputStream.readObject();
            if (received instanceof Map<?, ?>) {
                ((Map<?, ?>) received).forEach((src, msg) -> {
                    if (src instanceof DeviceUID && msg instanceof Map<?, ?>) {
                        receiveMessage((DeviceUID) src, (Map<CodePath, Object>) msg);
                    }
                });
            }
        }
    }

    private void receiveMessage(final DeviceUID src, final Map<CodePath, Object> msg) {
        messages.put(src, msg);
    }

    /**
     * Called by ProtelisVM read the stored messages.
     *
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
     *
     * @param toSend the message to be sent.
     */
    @Override
    public void shareState(final Map<CodePath, Object> toSend) {
        final Map<DeviceUID, Map<CodePath, Object>> msg = Stream.of(
                new AbstractMap.SimpleImmutableEntry<>(deviceUID, toSend)
        ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            try (ObjectOutput out = new ObjectOutputStream(bos)) {
                out.writeObject(msg);
                out.flush();
                final MqttMessage message = new MqttMessage(bos.toByteArray());
                neighbors.forEach(publish(message));
            }
        } catch (final IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private Consumer<String> publish(final MqttMessage message) {
        return topic -> {
            try {
                mqttClient.publish(topic, message).waitForCompletion();
            } catch (final MqttException e) {
                throw new IllegalStateException(e);
            }
        };
    }
}
