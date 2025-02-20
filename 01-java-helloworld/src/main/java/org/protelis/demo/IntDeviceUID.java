package org.protelis.demo;

import org.jetbrains.annotations.NotNull;
import org.protelis.lang.datatype.DeviceUID;

import java.util.Objects;

/**
 * DeviceUID implementation which uses an integer as unique key.
 */
public class IntDeviceUID implements DeviceUID, Comparable<IntDeviceUID> {

    private static final long serialVersionUID = 1L;
    private final int uid;

    /**
     * Constructor method.
     *
     * @param uid the id of the device
     */
    public IntDeviceUID(final int uid) {
        this.uid = uid;
    }

    /**
     * Getter for the id.
     *
     * @return an integer which represents the unique id.
     */
    public int getUid() {
        return uid;
    }

    /**
     * Implementation of the Comparable interfaces.
     *
     * @param o the object to be compared
     * @return a negative integer, zero, or a positive integer as this object is less than, equal to,
     *      or greater than the specified object.
     */
    @Override
    public int compareTo(@NotNull final IntDeviceUID o) {
        return equals(o) ? 0 : uid < o.getUid() ? -1 : 1;
    }

    /**
     * Compare this object with the specified object. Return true if and only if the uid value is the same.
     *
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
        final IntDeviceUID that = (IntDeviceUID) o;
        return uid == that.uid;
    }

    /**
     * Returns a hash code for the object.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(uid);
    }
}
