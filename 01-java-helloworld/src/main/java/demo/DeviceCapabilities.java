package demo;

import org.protelis.lang.datatype.DeviceUID;
import org.protelis.vm.CodePathFactory;
import org.protelis.vm.NetworkManager;
import org.protelis.vm.impl.AbstractExecutionContext;
import org.protelis.vm.impl.SimpleExecutionEnvironment;

public class DeviceCapabilities extends AbstractExecutionContext implements Speaker {

    private final IntDeviceUID uid;

    public DeviceCapabilities(final int uid, final NetworkManager netmgr) {
        super(new SimpleExecutionEnvironment(), netmgr);
        this.uid = new IntDeviceUID(uid);
    }

    public DeviceCapabilities(final int uid, final NetworkManager netmgr, CodePathFactory codePathFactory) {
        super(new SimpleExecutionEnvironment(), netmgr, codePathFactory);
        this.uid = new IntDeviceUID(uid);
    }

    @Override
    protected AbstractExecutionContext instance() {
        return new DeviceCapabilities(this.uid.getUID(), getNetworkManager());
    }

    @Override
    public DeviceUID getDeviceUID() {
        return uid;
    }

    @Override
    public Number getCurrentTime() {
        return System.currentTimeMillis();
    }

    @Override
    public double nextRandomDouble() {
        return Math.random();
    }
}
