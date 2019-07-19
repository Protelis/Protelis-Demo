package demo

import org.protelis.lang.datatype.DeviceUID

data class IntDeviceUID(val uid: Int) : DeviceUID, Comparable<IntDeviceUID> {
    override fun compareTo(other: IntDeviceUID) = uid.compareTo(other.uid)
    fun getUID() = uid
}