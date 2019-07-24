package demo;

import com.google.common.hash.Hashing;
import org.protelis.vm.NetworkManager;
import org.protelis.vm.ProtelisProgram;
import org.protelis.vm.ProtelisVM;
import org.protelis.vm.impl.HashingCodePathFactory;

public class Device {

    private final ProtelisVM vm;
    private final DeviceCapabilities deviceCapabilities;
    private final NetworkManager netmgr;

    public Device(ProtelisProgram program, int uid, NetworkManager netmgr, Speaker speaker) {
        this.netmgr = netmgr;
        this.deviceCapabilities = new DeviceCapabilities(uid, netmgr, new HashingCodePathFactory(Hashing.sha256()), speaker);
        this.vm = new ProtelisVM(program, deviceCapabilities);
    }

    public NetworkManager getNetworkManager() { return netmgr; }

    public DeviceCapabilities getDeviceCapabilities() {
        return deviceCapabilities;
    }

    public void runCycle() {
        this.vm.runCycle();
    }
}
