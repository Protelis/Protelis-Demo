package demo;

import com.google.common.collect.ImmutableSet;
import org.protelis.lang.datatype.DeviceUID;
import org.protelis.vm.CodePath;
import org.protelis.vm.NetworkManager;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Emulation of a network manager which uses the shared memory.
 */
public class EmulatedNetworkManager implements NetworkManager {

    private transient Map<DeviceUID, Map<CodePath, Object>> messages;
    private final DeviceUID deviceUID;
    private ImmutableSet<Device> neighbors;

    /**
     * Constructor method.
     * @param deviceUID the device id
     * @param neighbors the neighbors the device has to send his messages to
     */
    public EmulatedNetworkManager(final DeviceUID deviceUID, final Set<Device> neighbors) {
        this.deviceUID = deviceUID;
        this.neighbors = ImmutableSet.copyOf(neighbors);
        this.messages = new HashMap<>();
    }

    /**
     * Constructor method for devices with no neighbors.
     * @param deviceUID the device id
     */
    public EmulatedNetworkManager(final DeviceUID deviceUID) {
        this(deviceUID, Collections.emptySet());
    }

    /**
     * Getter for the device id.
     * @return the device id
     */
    public DeviceUID getDeviceUID() {
        return deviceUID;
    }

    /**
     * Getter for the neighbors.
     * @return the neighbors
     */
    public ImmutableSet<Device> getNeighbors() {
        return neighbors;
    }

    /**
     * Update the network manager with a new set of neighbors.
     * @param neighbors the new neighbors the device has to send his messages to
     */
    public void setNeighbors(final Set<Device> neighbors) {
        this.neighbors = ImmutableSet.copyOf(neighbors);
    }

    /**
     * Receives a message and stores it.
     * @param src the message source
     * @param msg the message content
     */
    private void receiveMessage(final DeviceUID src, final Map<CodePath, Object> msg) {
        messages.put(src, msg);
    }

    /**
     * Called by ProtelisVM read the stored messages.
     * @return the currently stored messages
     */
    @Override
    public Map<DeviceUID, Map<CodePath, Object>> getNeighborState() {
        Map<DeviceUID, Map<CodePath, Object>> t = messages;
        messages = new HashMap<>();
        return t;
    }

    /**
     * Called by ProtelisVM to send a message to the neighbors.
     * @param toSend the message to be sent.
     */
    @Override
    public void shareState(final Map<CodePath, Object> toSend) {
        neighbors.forEach(d -> ((EmulatedNetworkManager) d.getNetworkManager()).receiveMessage(deviceUID, toSend));
    }
}
