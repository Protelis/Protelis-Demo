package demo

import com.uchuhimo.konf.ConfigSpec

data class IPv4Host(val host: String, val port: Int)
data class ProtelisNode(val hostandport: IPv4Host, val id: Int, val neighbors: Set<IPv4Host>, val leader: Boolean = false)

object ProtelisConfigSpec : ConfigSpec("protelis") {
    val iterations by required<Int>(description = "Number of iterations the nodes have to run.")
    val protelisModuleName by required<String>()
    val nodes by required<List<ProtelisNode>>(description = "List of nodes")
}
