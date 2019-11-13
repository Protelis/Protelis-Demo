package demo

import com.uchuhimo.konf.ConfigSpec

/**
 * This is used by Konf to map the TOML objects ProtelisNode to a class.
 */
data class ProtelisNode(val id: Int, val listen: String, val neighbors: Set<String>, val leader: Boolean = false)

/**
 * This is used by Konf to map the TOML main configuration to a class.
 */
object ProtelisConfigSpec : ConfigSpec("protelis") {
    val iterations by required<Int>(description = "Number of iterations the nodes have to run.")
    val protelisModuleName by required<String>()
    val brokerHost by required<String>()
    val brokerPort by required<Int>()
    val nodes by required<List<ProtelisNode>>(description = "List of nodes")
}
