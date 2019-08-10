package demo;

import com.google.common.hash.Hashing;
import org.protelis.vm.NetworkManager;
import org.protelis.vm.ProtelisProgram;
import org.protelis.vm.ProtelisVM;
import org.protelis.vm.impl.HashingCodePathFactory;

/**
 * Simple protelis node.
 */
public class Device {

    private final ProtelisVM vm;
    private final DeviceCapabilities deviceCapabilities;
    private final NetworkManager netmgr;

    /**
     * Constructor method.
     * @param program the Program to be loaded
     * @param uid the unique identifier of this node
     * @param netmgr the network manager to use
     * @param speaker the speaker strategy
     */
    public Device(final ProtelisProgram program, final int uid, final NetworkManager netmgr, final Speaker speaker) {
        this.netmgr = netmgr;
        this.deviceCapabilities = new DeviceCapabilities(uid, netmgr, new HashingCodePathFactory(Hashing.sha256()), speaker);
        this.vm = new ProtelisVM(program, deviceCapabilities);
    }

    /**
     * Getter for the network manager.
     * @return the network manager
     */
    public NetworkManager getNetworkManager() {
        return netmgr;
    }

    /**
     * Getter for the device capabilities.
     * @return the device capabilities.
     */
    public DeviceCapabilities getDeviceCapabilities() {
        return deviceCapabilities;
    }

    /**
     * Runs a virtual machine cycle.
     */
    public void runCycle() {
        this.vm.runCycle();
    }
}
