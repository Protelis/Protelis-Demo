package org.protelis.demo

import com.google.common.hash.Hashing
import org.protelis.vm.NetworkManager
import org.protelis.vm.ProtelisProgram
import org.protelis.vm.ProtelisVM
import org.protelis.vm.impl.HashingCodePathFactory

/**
 * Models a single device, provided a program, a [networkManager], and a [speaker].
 */
class Device(
    program: ProtelisProgram,
    uid: Int,
    val networkManager: NetworkManager,
    private val speaker: Speaker,
) {
    /**
     * Capabilities of this device.
     */
    val deviceCapabilities = DeviceCapabilities(uid, networkManager, HashingCodePathFactory(Hashing.sha256()), speaker)
    private val vm = ProtelisVM(program, deviceCapabilities)

    /**
     * Runs a computation round.
     */
    fun runCycle() = this.vm.runCycle()
}
