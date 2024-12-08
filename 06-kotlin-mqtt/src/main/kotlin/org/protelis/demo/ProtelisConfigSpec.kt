package org.protelis.demo

import com.uchuhimo.konf.ConfigSpec

/**
 * A Protelis node with [id] listening to a certain [listen] address and with a pre-defined set of [neighbors].
 * May or may not be [leader] (defaults to `false`)
 */
data class ProtelisNode(
    val id: Int,
    val listen: String,
    val neighbors: Set<String>,
    val leader: Boolean = false,
)

/**
 * This is used by Konf to map the TOML main configuration to a class. Includes:
 * - number of [iterations] to perform;
 * - [protelisModuleName] to execute;
 * - [brokerHost] address and [brokerPort];
 * - list of [nodes] composing the system.
 */
object ProtelisConfigSpec : ConfigSpec("protelis") {
    /**
     * Number of iterations to perform.
     */
    val iterations by required<Int>(description = "Number of iterations the nodes have to run.")

    /**
     * Protelis module to execute.
     */
    val protelisModuleName by required<String>()

    /**
     * Host address.
     */
    val brokerHost by required<String>()

    /**
     * Host port.
     */
    val brokerPort by required<Int>()

    /**
     * Nodes in the system.
     */
    val nodes by required<List<ProtelisNode>>(description = "List of nodes")
}
