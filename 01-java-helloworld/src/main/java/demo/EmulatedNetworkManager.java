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

    private Map<DeviceUID, Map<CodePath, Object>> messages;
    private final DeviceUID uid;
    private ImmutableSet<Device> neighbors;

    /**
     * Constructor method.
     * @param uid the device id
     * @param neighbors the neighbors the device has to send his messages to
     */
    public EmulatedNetworkManager(final DeviceUID uid, final Set<Device> neighbors) {
        this.uid = uid;
        this.setNeighbors(neighbors);
        this.messages = new HashMap<>();
    }

    /**
     * Constructor method for devices with no neighbors.
     * @param uid the device id
     */
    public EmulatedNetworkManager(final DeviceUID uid) {
        this(uid, Collections.emptySet());
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
        neighbors.forEach(d -> ((EmulatedNetworkManager) d.getNetworkManager()).receiveMessage(uid, toSend));
    }
}
