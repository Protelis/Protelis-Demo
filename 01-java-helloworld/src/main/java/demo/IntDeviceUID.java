package demo;

import org.protelis.lang.datatype.DeviceUID;

public class IntDeviceUID implements DeviceUID, Comparable<DeviceUID>{

    private final int uid;

    public IntDeviceUID(final int uid) {
        this.uid = uid;
    }

    public int getUID() {
        return uid;
    }

    @Override
    public int compareTo(DeviceUID o) {
        if (this.getClass() == o.getClass()) {
            return this.getUID() - ((IntDeviceUID)o).getUID();
        } else {
            return 1;
        }
    }
}
