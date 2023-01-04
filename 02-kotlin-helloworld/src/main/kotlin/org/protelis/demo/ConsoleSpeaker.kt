package org.protelis.demo

class ConsoleSpeaker : Speaker {
    override fun announce(something: String) = println(something)
}
