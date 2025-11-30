package org.protelis.demo

import org.jgrapht.Graphs
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.DefaultUndirectedGraph
import org.protelis.lang.ProtelisLoader

/**
 * Application entrypoint.
 */
object HelloProtelis {
    private const val PROTELIS_MODULE_NAME = "hello"
    private const val DEVICE_COUNT = 3
    private val devices = ArrayList<Device>()

    /**
     * Application entrypoint.
     */
    fun main() {
        initializeNetwork()
        setLeader(0)
        syncRunNTimes(3)
    }

    private fun initializeNetwork() {
        // Initialize a graph
        val g = DefaultUndirectedGraph<Device, DefaultEdge>(DefaultEdge::class.java)
        // Initialize n nodes
        repeat(DEVICE_COUNT) {
            val program = ProtelisLoader.parse(PROTELIS_MODULE_NAME)
            val d = Device(program, it, EmulatedNetworkManager(IntDeviceUID(it)), ConsoleSpeaker())
            devices.add(d)
            g.addVertex(d)
        }
        // Link the nodes as a ring network
        repeat(DEVICE_COUNT) {
            g.addEdge(
                devices[it],
                devices[(it + 1) % DEVICE_COUNT],
            )
        }
        devices.forEach { (it.networkManager as EmulatedNetworkManager).neighbors = Graphs.neighborSetOf(g, it) }
    }

    private fun setLeader(id: Int) = devices[id].deviceCapabilities.executionEnvironment.put("leader", true)

    private fun syncRunNTimes(n: Int) = repeat(n) { _ ->
        devices.forEach { it.runCycle() }
    }
}
