package org.protelis.demo

import com.google.common.hash.Hashing
import org.protelis.demo.org.protelis.demo.DeviceCapabilities
import org.protelis.vm.NetworkManager
import org.protelis.vm.ProtelisProgram
import org.protelis.vm.ProtelisVM
import org.protelis.vm.impl.HashingCodePathFactory

class Device(program: ProtelisProgram, uid: Int, val netmgr: NetworkManager, private val speaker: Speaker) {
    val deviceCapabilities = DeviceCapabilities(uid, netmgr, HashingCodePathFactory(Hashing.sha256()), speaker)
    private val vm = ProtelisVM(program, deviceCapabilities)

    fun runCycle() = this.vm.runCycle()
}
