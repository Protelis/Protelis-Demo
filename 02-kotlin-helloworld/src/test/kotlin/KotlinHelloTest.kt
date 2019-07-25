
package demo

import io.kotlintest.Spec
import io.kotlintest.TestCase
import io.kotlintest.matchers.exactly
import io.kotlintest.specs.FunSpec
import io.kotlintest.specs.StringSpec
import io.mockk.confirmVerified
import io.mockk.spyk
import io.mockk.verify
import org.jgrapht.Graphs
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.DefaultUndirectedGraph
import org.protelis.lang.ProtelisLoader

class HelloProtelisTests : StringSpec() {

    val protelisModuleName = "hello"
    val n = 3
    val iterations = 4
    var devices: List<Device> = emptyList()
    var speakers: List<Speaker> = emptyList()
    val leader = 0

    override fun beforeSpec(spec: Spec) {
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
                    devices[(it + 1) % n])
        }

        devices.forEach { (it.netmgr as EmulatedNetworkManager).neighbors = Graphs.neighborSetOf(g, it) }
        devices[leader].deviceCapabilities.executionEnvironment.put("leader", true)

        repeat(iterations) {
            devices.forEach { it.runCycle() }
        }
    }

    init {
        "The leader count should be correct" {
            generateSequence(3f) { it - 1 }
                    .take(iterations)
                    .map { "The leader's count is: ${it}" }
                    .forEach {
                        verify(exactly = 1) { speakers[leader].announce(it) }
                    }
        }

        "The leader should be at ${leader}" {
            verify(exactly = iterations) { speakers[leader].announce("The leader is at ${leader}") }
        }

        "The leader neighbors should say something" {
            sequenceOf(leader)
                    .flatMap { sequenceOf((leader + n -1) % n, (leader + 1) % n) }
                    .forEach { verify(exactly = iterations) { speakers[it].announce("Hello from the leader to its neighbor at ${it}") } }
        }
    }
}