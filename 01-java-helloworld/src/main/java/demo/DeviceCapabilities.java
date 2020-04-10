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
public class DeviceCapabilities extends AbstractExecutionContext<DeviceCapabilities> implements Speaker {

    private final IntDeviceUID deviceUID;
    private final Speaker speaker;

    /**
     * Constructor method.
     * @param deviceUID the device id
     * @param netmgr the device network manager
     * @param speaker the speaker strategy
     */
    public DeviceCapabilities(final int deviceUID, final NetworkManager netmgr, final Speaker speaker) {
        super(new SimpleExecutionEnvironment(), netmgr);
        this.deviceUID = new IntDeviceUID(deviceUID);
        this.speaker = speaker;
    }

    /**
     * Constructor method with CodePathFactory.
     * @param deviceUID the device id
     * @param netmgr the device network manager
     * @param codePathFactory the code path factory
     * @param speaker the speaker strategy
     */
    public DeviceCapabilities(final int deviceUID, final NetworkManager netmgr, final CodePathFactory codePathFactory,
                              final Speaker speaker) {
        super(new SimpleExecutionEnvironment(), netmgr, codePathFactory);
        this.deviceUID = new IntDeviceUID(deviceUID);
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
    protected DeviceCapabilities instance() {
        return new DeviceCapabilities(this.deviceUID.getUid(), getNetworkManager(), speaker);
    }

    /**
     * Getter for the device id.
     * @return the device id
     */
    @Override
    public DeviceUID getDeviceUID() {
        return deviceUID;
    }

    /**
     * Getter for the speaker.
     * @return the speaker
     */
    public Speaker getSpeaker() {
        return speaker;
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
