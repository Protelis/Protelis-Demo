package org.protelis.demo

import com.uchuhimo.konf.Config
import com.uchuhimo.konf.source.toml
import org.protelis.lang.ProtelisLoader

/**
 * Application entry point.
 */
object HelloProtelis {
    private var devices: List<Device> = emptyList()
    private val config =
        Config { addSpec(ProtelisConfigSpec) }
            .from.toml
            .resource("config.toml")
    private val protelisModuleName = config[ProtelisConfigSpec.protelisModuleName]
    private val iterations = config[ProtelisConfigSpec.iterations]
    private val nodes = config[ProtelisConfigSpec.nodes]

    /**
     * Application entrypoint method.
     */
    fun main() {
        // Initialize some nodes.
        nodes.forEach {
            val socketNetworkManager =
                SocketNetworkManager(IntDeviceUID(it.id), it.hostandport.port, it.neighbors)
                    .apply { listen() }
            val program = ProtelisLoader.parse(protelisModuleName)
            val node = Device(program, it.id, socketNetworkManager, ConsoleSpeaker())
            if (it.leader) {
                node.deviceCapabilities.executionEnvironment.put("leader", true)
            }
            devices += node
        }
        // Let the nodes make some iterations.
        repeat(iterations) { _ ->
            devices.forEach { it.runCycle() }
        }
        // Close the thread listening.
        devices.forEach { (it.networkManager as SocketNetworkManager).stop() }
    }
}
