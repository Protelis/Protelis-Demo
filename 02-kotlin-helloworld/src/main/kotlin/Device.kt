package demo

import com.google.common.hash.Hashing
import org.protelis.vm.ProtelisProgram
import org.protelis.vm.ProtelisVM
import org.protelis.vm.impl.HashingCodePathFactory

class Device(program: ProtelisProgram, uid: Int, val netmgr: MyNetworkManager) {
    val deviceCapabilities = DeviceCapabilities(uid, netmgr, HashingCodePathFactory(Hashing.sha256()))
    private val vm = ProtelisVM(program, deviceCapabilities)

    fun runCycle() = this.vm.runCycle()
    fun sendMessages() = this.netmgr.sendMessages()
}