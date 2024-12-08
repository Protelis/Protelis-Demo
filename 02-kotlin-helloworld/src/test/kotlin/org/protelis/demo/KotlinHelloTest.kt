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
        const val PROTELIS_MODULE_NAME = "hello"
        const val N = 3
        const val ITERATIONS = 4
        const val LEADER = 0
    }

    private var devices: List<Device> = emptyList()
    private var speakers: List<Speaker> = emptyList()

    override suspend fun beforeSpec(spec: Spec) {
        val g = DefaultUndirectedGraph<Device, DefaultEdge>(DefaultEdge::class.java)
        repeat(N) {
            val program = ProtelisLoader.parse(PROTELIS_MODULE_NAME)
            val s = spyk(ConsoleSpeaker())
            val d = Device(program, it, EmulatedNetworkManager(IntDeviceUID(it)), s)
            devices = devices + d
            speakers = speakers + s
            g.addVertex(d)
        }
        repeat(N) {
            g.addEdge(
                devices[it],
                devices[(it + 1) % N],
            )
        }
        // Let every device know its neighbors and set the leader
        devices.forEach { (it.networkManager as EmulatedNetworkManager).neighbors = Graphs.neighborSetOf(g, it) }
        devices[LEADER].deviceCapabilities.executionEnvironment.put("leader", true)
        // Run some cycles
        repeat(ITERATIONS) { _ ->
            devices.forEach { it.runCycle() }
        }
    }

    init {
        "The leader count should be correct" {
            generateSequence(3f) { it - 1 }
                .take(ITERATIONS)
                .map { "The leader's count is: $it" }
                .forEach {
                    verify(exactly = 1) { speakers[LEADER].announce(it) }
                }
        }
        "The leader should be at $LEADER" {
            verify(exactly = ITERATIONS) { speakers[LEADER].announce("The leader is at $LEADER") }
        }
        "The leader neighbors should say something" {
            sequenceOf(LEADER)
                .flatMap { sequenceOf((LEADER + N - 1) % N, (LEADER + 1) % N) }
                .forEach {
                    verify(exactly = ITERATIONS) {
                        speakers[it].announce("Hello from the leader to its neighbor at $it")
                    }
                }
        }
    }
}
