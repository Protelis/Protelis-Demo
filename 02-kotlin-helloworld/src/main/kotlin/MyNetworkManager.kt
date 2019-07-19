package demo

import org.protelis.vm.NetworkManager

interface MyNetworkManager : NetworkManager {
    fun sendMessages()
}