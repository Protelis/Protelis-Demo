package org.protelis.demo

import org.protelis.lang.datatype.DeviceUID

/**
 * Device identifier based on a [Int] [uid].
 */
data class IntDeviceUID(val uid: Int) : DeviceUID, Comparable<IntDeviceUID> {
    override fun compareTo(other: IntDeviceUID) = uid.compareTo(other.uid)
}
