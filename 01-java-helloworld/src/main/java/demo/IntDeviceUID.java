package demo;

import org.protelis.lang.datatype.DeviceUID;

/**
 * DeviceUID implementation which uses an integer as unique key.
 */
public class IntDeviceUID implements DeviceUID, Comparable<DeviceUID> {

    private final int uid;

    /**
     * Constructor method.
     * @param uid the id of the device
     */
    public IntDeviceUID(final int uid) {
        this.uid = uid;
    }

    /**
     * Getter for the id.
     * @return an integer which represents the unique id.
     */
    public int getUID() {
        return uid;
    }

    /**
     * Implementation of the Comparable interfacec.
     * @param o the object to be compared
     * @return a negative integer, zero, or a positive integer as this object is less than, equal to,
     *      or greater than the specified object.
     */
    @Override
    public int compareTo(final DeviceUID o) {
        if (this.getClass() == o.getClass()) {
            return this.getUID() - ((IntDeviceUID) o).getUID();
        } else {
            return 1;
        }
    }
}
