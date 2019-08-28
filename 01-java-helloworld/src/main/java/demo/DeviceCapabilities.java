package demo;

import org.protelis.lang.datatype.DeviceUID;
import org.protelis.vm.CodePathFactory;
import org.protelis.vm.NetworkManager;
import org.protelis.vm.impl.AbstractExecutionContext;
import org.protelis.vm.impl.SimpleExecutionEnvironment;

/**
 * Implementation of an Execution Context. It extends the existing AbstractExecutionContext. It is needed to execute
 * a Protelis program. Each device should have its DeviceCapabilities instance.
 */
public class DeviceCapabilities extends AbstractExecutionContext implements Speaker {

    private final IntDeviceUID uid;
    private final Speaker speaker;

    /**
     * Constructor method.
     * @param uid the device id
     * @param netmgr the device network manager
     * @param speaker the speaker strategy
     */
    public DeviceCapabilities(final int uid, final NetworkManager netmgr, final Speaker speaker) {
        super(new SimpleExecutionEnvironment(), netmgr);
        this.uid = new IntDeviceUID(uid);
        this.speaker = speaker;
    }

    /**
     * Constructor method with CodePathFactory.
     * @param uid the device id
     * @param netmgr the device network manager
     * @param codePathFactory the code path factory
     * @param speaker the speaker strategy
     */
    public DeviceCapabilities(final int uid, final NetworkManager netmgr, final CodePathFactory codePathFactory,
                              final Speaker speaker) {
        super(new SimpleExecutionEnvironment(), netmgr, codePathFactory);
        this.uid = new IntDeviceUID(uid);
        this.speaker = speaker;
    }

    /**
     * Strategy pattern for the Speaker interface.
     */
    @Override
    public void announce(final String message) {
        speaker.announce(message);
    }

    /**
     * Returns a instance.
     * @return new instance of device capabilities.
     */
    @Override
    protected AbstractExecutionContext instance() {
        return new DeviceCapabilities(this.uid.getUID(), getNetworkManager(), speaker);
    }

    /**
     * Getter for the device id.
     * @return the device id
     */
    @Override
    public DeviceUID getDeviceUID() {
        return uid;
    }

    /**
     * Returns the current device time.
     * @return the current device time
     */
    @Override
    public Number getCurrentTime() {
        return System.currentTimeMillis();
    }

    /**
     * Returns a pseudo-random number.
     * @return a pseudo-random number.
     */
    @Override
    public double nextRandomDouble() {
        return Math.random();
    }
}
