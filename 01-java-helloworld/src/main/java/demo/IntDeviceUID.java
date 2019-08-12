package demo;

import org.protelis.lang.datatype.DeviceUID;

import java.util.Objects;

/**
 * DeviceUID implementation which uses an integer as unique key.
 */
public class IntDeviceUID implements DeviceUID, Comparable<IntDeviceUID> {

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
     * Implementation of the Comparable interfaces.
     * @param o the object to be compared
     * @return a negative integer, zero, or a positive integer as this object is less than, equal to,
     *      or greater than the specified object.
     */
    @Override
    public int compareTo(final IntDeviceUID o) {
        return equals(o) ? 0 : ((uid < o.getUID()) ? -1 : 1);
    }

    /**
     * Compare this object with the specified object. Return true if and only if the uid value is the same.
     * @param o the object to compare with.
     * @return true if the objects are the same; false otherwise.
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        IntDeviceUID that = (IntDeviceUID) o;
        return uid == that.uid;
    }

    /**
     * Returns a hash code for the object.
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(uid);
    }
}
