package demo

import com.uchuhimo.konf.Config
import com.uchuhimo.konf.source.toml
import org.protelis.lang.ProtelisLoader

/**
 * Demonstration of the [MqttNetworkManager] usage.
 */
fun main() {
    var devices: List<Device> = emptyList()
    val config = Config { addSpec(ProtelisConfigSpec) }
            .from.toml.resource("config.toml")
    val protelisModuleName = config[ProtelisConfigSpec.protelisModuleName]
    val iterations = config[ProtelisConfigSpec.iterations]
    val nodes = config[ProtelisConfigSpec.nodes]
    // Initialize some nodes.
    nodes.forEach {
        val mqttNetworkManager = MqttNetworkManager(IntDeviceUID(it.id), it.neighbors).apply { listen(it.listen) }
        val program = ProtelisLoader.parse(protelisModuleName)
        val node = Device(program, it.id, mqttNetworkManager, ConsoleSpeaker())
        if (it.leader) {
            node.deviceCapabilities.executionEnvironment.put("leader", true)
        }
        devices += node
    }
    // Let the nodes make some iterations.
    repeat(iterations) {
        devices.forEach { it.runCycle() }
    }
    // Close the thread listening.
    devices.forEach { (it.netmgr as MqttNetworkManager).stop() }
}