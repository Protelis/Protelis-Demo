package org.protelis.demo.org.protelis.demo

import org.protelis.demo.IntDeviceUID
import org.protelis.demo.Speaker
import org.protelis.vm.CodePathFactory
import org.protelis.vm.NetworkManager
import org.protelis.vm.impl.AbstractExecutionContext
import org.protelis.vm.impl.SimpleExecutionEnvironment

class DeviceCapabilities(
    private val uid: Int,
    private val netmgr: NetworkManager,
    private val codePathFactory: CodePathFactory,
    private val speaker: Speaker
) : AbstractExecutionContext<DeviceCapabilities>(SimpleExecutionEnvironment(), netmgr, codePathFactory), Speaker {

    override fun announce(something: String) = speaker.announce(something)

    private val myUID = IntDeviceUID(uid)

    override fun nextRandomDouble() = Math.random()

    override fun getDeviceUID() = myUID

    override fun getCurrentTime() = System.currentTimeMillis()

    override fun instance() = DeviceCapabilities(uid, netmgr, codePathFactory, speaker)
}
