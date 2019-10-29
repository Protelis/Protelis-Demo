package demo

import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttAsyncClient
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import org.protelis.lang.datatype.DeviceUID
import org.protelis.vm.CodePath
import org.protelis.vm.NetworkManager
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class MqttNetworkManager(private val uid: DeviceUID, address: String, port: Int, private val qos: Int, private val neighbors: Set<String>) : NetworkManager {
    private var messages: Map<DeviceUID, Map<CodePath, Any>> = emptyMap()
    private val broker = "tcp://$address:$port"
    private var mqttClient = MqttAsyncClient(broker, uid.hashCode().toString(), MemoryPersistence())

    constructor(uid: DeviceUID, neighbors: Set<String>) : this(uid, "127.0.0.1", 1883, 2, neighbors)

    fun listen(topic: String): IMqttToken {
        mqttClient.connect().waitForCompletion()
        return mqttClient.subscribe(topic, qos, null, null, this::handleMessage)
    }

    @Suppress("UNCHECKED_CAST")
    private fun handleMessage(@Suppress("UNUSED_PARAMETER") topic: String, message: MqttMessage) {
        ObjectInputStream(ByteArrayInputStream(message.payload)).use {
            val received = it.readObject()
            if (received is Map<*, *>) {
                received.forEach { src, msg -> receiveMessage(src as DeviceUID, msg as Map<CodePath, Any>) }
            }
        }
    }

    private fun receiveMessage(src: DeviceUID, msg: Map<CodePath, Any>) {
        messages += Pair(src, msg)
    }

    fun stop() = mqttClient.disconnect()
    /**
     * Called by [ProtelisVM] during execution to send its current shared
     * state to neighbors. The call is serial within the execution, so this
     * should probably queue up a message to be sent, rather than actually
     * carrying out a lengthy operations during this call.
     *
     * @param toSend
     * Shared state to be transmitted to neighbors.
     */
    override fun shareState(toSend: MutableMap<CodePath, Any>?) {
        val message = mapOf(Pair(uid, toSend))
        val bos = ByteArrayOutputStream()
        ObjectOutputStream(bos).use {
            it.writeObject(message)
            it.flush()
            val mqttMessage = MqttMessage(bos.toByteArray())
            neighbors.forEach(publish(mqttMessage))
        }
    }

    private fun publish(message: MqttMessage) = fun(topic: String) {
        mqttClient.publish(topic, message).waitForCompletion()
    }

    /**
     * Called by [ProtelisVM] during execution to collect the most recent
     * information available from neighbors. The call is serial within the
     * execution, so this should probably poll state maintained by a separate
     * thread, rather than gathering state during this call.
     *
     * @return A map associating each neighbor with its shared state. The object
     * returned should not be modified, and [ProtelisVM] will not
     * change it either.
     */
    override fun getNeighborState(): Map<DeviceUID, Map<CodePath, Any>> =
            messages.apply { messages = emptyMap() }
}