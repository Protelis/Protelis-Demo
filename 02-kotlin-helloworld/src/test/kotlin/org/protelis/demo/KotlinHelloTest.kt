package org.protelis.demo

import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.StringSpec
import io.mockk.spyk
import io.mockk.verify
import org.jgrapht.Graphs
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.DefaultUndirectedGraph
import org.protelis.lang.ProtelisLoader

class KotlinHelloTest : StringSpec() {

    companion object {
        const val protelisModuleName = "hello"
        const val n = 3
        const val iterations = 4
        const val leader = 0
    }

    private var devices: List<Device> = emptyList()
    private var speakers: List<Speaker> = emptyList()

    override suspend fun beforeSpec(spec: Spec) {
        val g = DefaultUndirectedGraph<Device, DefaultEdge>(DefaultEdge::class.java)
        repeat(n) {
            val program = ProtelisLoader.parse(protelisModuleName)
            val s = spyk(ConsoleSpeaker())
            val d = Device(program, it, EmulatedNetworkManager(IntDeviceUID(it)), s)
            devices = devices + d
            speakers = speakers + s
            g.addVertex(d)
        }
        repeat(n) {
            g.addEdge(
                devices[it],
                devices[(it + 1) % n]
            )
        }
        // Let every device know its neighbors and set the leader
        devices.forEach { (it.networkManager as EmulatedNetworkManager).neighbors = Graphs.neighborSetOf(g, it) }
        devices[leader].deviceCapabilities.executionEnvironment.put("leader", true)
        // Run some cycles
        repeat(iterations) {
            devices.forEach { it.runCycle() }
        }
    }

    init {
        "The leader count should be correct" {
            generateSequence(3f) { it - 1 }
                .take(iterations)
                .map { "The leader's count is: $it" }
                .forEach {
                    verify(exactly = 1) { speakers[leader].announce(it) }
                }
        }
        "The leader should be at $leader" {
            verify(exactly = iterations) { speakers[leader].announce("The leader is at $leader") }
        }
        "The leader neighbors should say something" {
            sequenceOf(leader)
                .flatMap { sequenceOf((leader + n - 1) % n, (leader + 1) % n) }
                .forEach {
                    verify(exactly = iterations) {
                        speakers[it].announce("Hello from the leader to its neighbor at $it")
                    }
                }
        }
    }
}
