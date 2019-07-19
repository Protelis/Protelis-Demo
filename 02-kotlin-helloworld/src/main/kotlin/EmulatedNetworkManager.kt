package demo

import org.protelis.lang.datatype.DeviceUID
import org.protelis.vm.CodePath

class EmulatedNetworkManager(private val uid : DeviceUID, var neighbors: Set<Device> = emptySet()) : MyNetworkManager {

    private var toBeSent: Map<CodePath, Any> = emptyMap()
    private var messages: Map<DeviceUID, Map<CodePath, Any>> = emptyMap()

    private fun receiveMessage(src: DeviceUID, msg: Map<CodePath, Any>) {
        messages += Pair(src, msg)
    }

    override fun sendMessages() {
        if (toBeSent.isNotEmpty()) {
            neighbors.forEach { (it.netmgr as EmulatedNetworkManager).receiveMessage(uid, toBeSent) }
        }
    }

    override fun getNeighborState() : Map<DeviceUID, Map<CodePath, Any>> =
        messages.apply { messages = emptyMap() }


    override fun shareState(toSend: Map<CodePath, Any>) { toBeSent = toSend }

}