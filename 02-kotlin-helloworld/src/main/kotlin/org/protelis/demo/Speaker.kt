package org.protelis.demo

/**
 * Interaction with the external world.
 */
interface Speaker {

    /**
     * Outputs something in [String] format.
     */
    fun announce(something: String)
}
