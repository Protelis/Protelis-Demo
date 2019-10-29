package demo

import com.uchuhimo.konf.Config
import org.protelis.lang.ProtelisLoader
import org.protelis.vm.NetworkManager

class Main {

    private var devices: List<Device> = emptyList()
    private val config = Config { addSpec(ProtelisConfigSpec) }
            .from.toml.resource("config.toml")
    private val protelisModuleName = config[ProtelisConfigSpec.protelisModuleName]
    private val iterations = config[ProtelisConfigSpec.iterations]
    private val nodes = config[ProtelisConfigSpec.nodes]

    fun hello() {
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
}

fun main() {
    Main().hello()
}