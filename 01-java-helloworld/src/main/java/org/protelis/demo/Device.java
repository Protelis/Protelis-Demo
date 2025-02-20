package org.protelis.demo;

import com.google.common.hash.Hashing;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.protelis.vm.NetworkManager;
import org.protelis.vm.ProtelisProgram;
import org.protelis.vm.ProtelisVM;
import org.protelis.vm.impl.HashingCodePathFactory;

/**
 * Simple protelis node.
 */
public class Device {

    private final transient ProtelisVM vm;
    private final DeviceCapabilities deviceCapabilities;
    private final NetworkManager networkManager;

    /**
     * Constructor method.
     *
     * @param program the Program to be loaded
     * @param uid the unique identifier of this node
     * @param networkManager the network manager to use
     * @param speaker the speaker strategy
     */
    public Device(final ProtelisProgram program, final int uid, final NetworkManager networkManager, final Speaker speaker) {
        this.networkManager = networkManager;
        this.deviceCapabilities =
            new DeviceCapabilities(uid, networkManager, new HashingCodePathFactory(Hashing.sha256()), speaker);
        this.vm = new ProtelisVM(program, deviceCapabilities);
    }

    /**
     * Getter for the network manager.
     *
     * @return the network manager
     */
    public NetworkManager getNetworkManager() {
        return networkManager;
    }

    /**
     * Getter for the device capabilities.
     *
     * @return the device capabilities.
     */
    @SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "Done by purpose")
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
