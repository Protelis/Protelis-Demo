package org.protelis.demo

import org.protelis.vm.CodePathFactory
import org.protelis.vm.NetworkManager
import org.protelis.vm.impl.AbstractExecutionContext
import org.protelis.vm.impl.SimpleExecutionEnvironment

/**
 * Represents the capabilities of a device with:
 * - unique identifier ([uid];
 * - network communication via [networkManager];
 * - internal alignment control via [codePathFactory];
 * - possibility to do actuation via a [speaker].
 */
class DeviceCapabilities(
    private val uid: Int,
    private val networkManager: NetworkManager,
    private val codePathFactory: CodePathFactory,
    private val speaker: Speaker,
) : AbstractExecutionContext<DeviceCapabilities>(SimpleExecutionEnvironment(), networkManager, codePathFactory),
    Speaker {
    override fun announce(something: String) = speaker.announce(something)

    private val myUID = IntDeviceUID(uid)

    override fun nextRandomDouble() = Math.random()

    override fun getDeviceUID() = myUID

    override fun getCurrentTime() = System.currentTimeMillis()

    override fun instance() = DeviceCapabilities(uid, networkManager, codePathFactory, speaker)
}
