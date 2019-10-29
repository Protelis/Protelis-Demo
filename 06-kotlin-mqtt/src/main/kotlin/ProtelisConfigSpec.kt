package demo

import com.uchuhimo.konf.ConfigSpec

data class ProtelisNode(val id: Int, val listen: String, val neighbors: Set<String>, val leader: Boolean = false)

object ProtelisConfigSpec : ConfigSpec("protelis") {
    val iterations by required<Int>(description = "Number of iterations the nodes have to run.")
    val protelisModuleName by required<String>()
    val brokerHost by required<String>()
    val brokerPort by required<Int>()
    val nodes by required<List<ProtelisNode>>(description = "List of nodes")
}
