package demo

import org.protelis.vm.CodePathFactory
import org.protelis.vm.NetworkManager
import org.protelis.vm.impl.AbstractExecutionContext
import org.protelis.vm.impl.SimpleExecutionEnvironment

class DeviceCapabilities(private val uid: Int, private val netmgr: NetworkManager, private val codePathFactory: CodePathFactory) :
        AbstractExecutionContext(SimpleExecutionEnvironment(), netmgr, codePathFactory),
        Speaker {
    private val myUID = IntDeviceUID(uid)

    override fun nextRandomDouble() = Math.random()

    override fun getDeviceUID() = myUID

    override fun getCurrentTime() =  System.currentTimeMillis()

    override fun instance() = DeviceCapabilities(uid, netmgr, codePathFactory)

}