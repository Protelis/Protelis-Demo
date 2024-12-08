package org.protelis.demo

import org.protelis.lang.datatype.DeviceUID
import org.protelis.vm.CodePath
import org.protelis.vm.NetworkManager

/**
 * A [NetworkManager] emulating a network locally, requires a unique identifier ([uid]) and the set of [neighbors].
 */
class EmulatedNetworkManager(
    private val uid: DeviceUID,
    var neighbors: Set<Device> = emptySet(),
) : NetworkManager {
    private var messages: Map<DeviceUID, Map<CodePath, Any>> = emptyMap()

    private fun receiveMessage(
        src: DeviceUID,
        msg: Map<CodePath, Any>,
    ) {
        messages += Pair(src, msg)
    }

    override fun getNeighborState(): Map<DeviceUID, Map<CodePath, Any>> = messages.apply { messages = emptyMap() }

    override fun shareState(toSend: Map<CodePath, Any>) {
        if (toSend.isNotEmpty()) {
            neighbors.forEach { (it.networkManager as EmulatedNetworkManager).receiveMessage(uid, toSend) }
        }
    }
}
