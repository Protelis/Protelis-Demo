package demo;

import com.google.common.collect.ImmutableSet;
import java8.util.Sets;
import org.protelis.lang.datatype.DeviceUID;
import org.protelis.vm.CodePath;
import org.protelis.vm.NetworkManager;

import java.util.*;

public class EmulatedNetworkManager implements NetworkManager {

    private Map<DeviceUID, Map<CodePath, Object>> messages;
    private final DeviceUID uid;
    private ImmutableSet<Device> neighbors;

    public EmulatedNetworkManager(final DeviceUID uid, final Set<Device> neighbors) {
        this.uid = uid;
        this.setNeighbors(neighbors);
        this.messages = new HashMap<>();
    }

    public EmulatedNetworkManager(final DeviceUID uid) {
        this(uid, Collections.emptySet());
    }

    public void setNeighbors(final Set<Device> neighbors) {
        this.neighbors = ImmutableSet.copyOf(neighbors);
    }

    private void receiveMessage(DeviceUID src, Map<CodePath, Object> msg) {
        messages.put(src, msg);
    }

    @Override
    public Map<DeviceUID, Map<CodePath, Object>> getNeighborState() {
        Map<DeviceUID, Map<CodePath, Object>> t = messages;
        messages = new HashMap<>();
        return t;
    }

    @Override
    public void shareState(Map<CodePath, Object> toSend) {
        neighbors.forEach(d -> ((EmulatedNetworkManager)d.getNetworkManager()).receiveMessage(uid, toSend));
    }
}
