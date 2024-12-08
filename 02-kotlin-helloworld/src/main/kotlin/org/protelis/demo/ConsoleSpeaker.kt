package org.protelis.demo

/**
 * Trivial speaker implemnetation using [println].
 */
class ConsoleSpeaker : Speaker {
    override fun announce(something: String) = println(something)
}
