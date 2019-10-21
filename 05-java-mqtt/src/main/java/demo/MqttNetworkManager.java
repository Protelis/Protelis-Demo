package demo;

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
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Network Manager which uses the MQTT protocol.
 */
public class MqttNetworkManager implements NetworkManager {

    private static final String DEFAULT_ADDRESS = "127.0.0.1";
    private static final int DEFAULT_PORT = 1883;
    private static final int DEFAULT_QOS = 2;
    private final DeviceUID deviceUID;
    private final String address;
    private final int port;
    private final String clientId;
    private final List<String> neighbors;
    private final int qos;
    private IMqttAsyncClient mqttClient;
    private transient Map<DeviceUID, Map<CodePath, Object>> messages = new HashMap<>();

    /**
     * Constructor method for which uses the default config.
     * @param deviceUID the device id.
     * @param neighbors the node neighbors.
     */
    public MqttNetworkManager(final DeviceUID deviceUID, final List<String> neighbors) {
        this(deviceUID, DEFAULT_ADDRESS, DEFAULT_PORT, neighbors);
    }

    /**
     * Constructor method to explicitly specify the broker addess and port.
     * @param deviceUID the device id.
     * @param address the MQTT broker address.
     * @param port the MQTT broker port.
     * @param neighbors the node neighbors.
     */
    public MqttNetworkManager(final DeviceUID deviceUID, final String address, final int port,
                              final List<String> neighbors) {
        this(deviceUID, address, port, DEFAULT_QOS, neighbors);
    }

    /**
     * Constructor method to explicitly set the MQTT qos.
     * @param deviceUID the device id.
     * @param address the MQTT broker address.
     * @param port the MQTT broker port.
     * @param qos the MQTT qos parameter.
     * @param neighbors the node neighbors.
     */
    public MqttNetworkManager(final DeviceUID deviceUID, final String address, final int port,
                              final int qos, final List<String> neighbors) {
        this.deviceUID = deviceUID;
        this.address = address;
        this.port = port;
        this.clientId = Integer.toString(deviceUID.hashCode());
        this.qos = qos;
        this.neighbors = new ArrayList<>(neighbors);
    }

    /**
     * Starts the MQTT client and subscribes to the target topic.
     * @param topic the topic the node is listening.
     * @throws MqttException if some MQTT related error occur.
     * @return the token to track the listen asynchronous operation.
     */
    public IMqttToken listen(final String topic) throws MqttException {
        final String broker = "tcp://" + this.address + ":" + this.port;
        final MqttClientPersistence persistence = new MemoryPersistence();
        this.mqttClient = new MqttAsyncClient(broker, this.clientId, persistence);
        mqttClient.connect().waitForCompletion();
        return mqttClient.subscribe(topic, this.qos, null, null, this::handleMessage);
    }

    /**
     * Shutdowns the MQTT client.
     * @return the token to track the asynchronous task
     * @throws MqttException if some MQTT related error occur
     */
    public IMqttToken stop() throws MqttException {
        return this.mqttClient.disconnect();
    }

    @SuppressWarnings("PMD.UnusedFormalParameter")
    private void handleMessage(final String topic, final MqttMessage message) throws IOException, ClassNotFoundException {
        final Object received = new ObjectInputStream(new ByteArrayInputStream(message.getPayload()))
                .readObject();
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
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(msg);
            out.flush();
            final MqttMessage message = new MqttMessage(bos.toByteArray());
            neighbors.forEach(publish(message));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Consumer<String> publish(final MqttMessage message) {
        return topic -> {
            try {
                mqttClient.publish(topic, message).waitForCompletion();
            } catch (MqttException e) {
                e.printStackTrace();
            }
        };
    }
}
