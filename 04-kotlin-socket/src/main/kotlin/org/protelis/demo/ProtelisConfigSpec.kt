package org.protelis.demo

import com.uchuhimo.konf.ConfigSpec

/**
 * An IPv4 [host] with its [port].
 */
data class IPv4Host(
    val host: String,
    val port: Int,
)

/**
 * A networked Protelis node with IPv4 [hostandport], an [id], and a predefined set of [neighbors].
 * It can possibly be a [leader] (defaults to `false`)
 */
data class ProtelisNode(
    val hostandport: IPv4Host,
    val id: Int,
    val neighbors: Set<IPv4Host>,
    val leader: Boolean = false,
)

/**
 * Demo configuration.
 */
object ProtelisConfigSpec : ConfigSpec("protelis") {
    /**
     * Iterations to be performed.
     */
    val iterations by required<Int>(description = "Number of iterations the devices will run.")

    /**
     * Protelis program to be executed.
     */
    val protelisModuleName by required<String>(description = "Protelis program to be executed")

    /**
     * Nodes participating in the system.
     */
    val nodes by required<List<ProtelisNode>>(description = "List of nodes")
}
