package org.protelis.demo;

import com.uchuhimo.konf.BaseConfig;
import com.uchuhimo.konf.Config;
import com.uchuhimo.konf.source.DefaultTomlLoaderKt;
import org.protelis.demo.data.MqttProtelisNode;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.protelis.lang.ProtelisLoader;
import org.protelis.vm.ProtelisProgram;

import java.util.ArrayList;
import java.util.List;

/**
 * Entrypoint for the MQTT demo.
 */
public final class HelloProtelis {

    private HelloProtelis() { }

    /**
     * Main method.
     * @param args unused.
     */
    public static void main(final String[] args) {
        Config config = new BaseConfig();
        config.addSpec(ProtelisConfigSpec.SPEC);
        config = DefaultTomlLoaderKt.getToml(config.from()).resource("config.toml", false);
        final String protelisModuleName = config.get(ProtelisConfigSpec.protelisModuleName);
        final int iterations = config.get(ProtelisConfigSpec.iterations);
        final List<MqttProtelisNode> nodes = config.get(ProtelisConfigSpec.nodes);
        final List<Device> devices = new ArrayList<>();
        // Initialize each node
        nodes.forEach(n -> {
            final MqttNetworkManager netmgr = new MqttNetworkManager(new IntDeviceUID(n.getId()), n.getNeighbors());
            try {
                netmgr.listen(n.getListen()).waitForCompletion();
            } catch (MqttException e) {
                throw new IllegalStateException(e);
            }
            final ProtelisProgram program = ProtelisLoader.parse(protelisModuleName);
            final Device node = new Device(program, n.getId(), netmgr, new ConsoleSpeaker());
            if (n.isLeader()) {
                node.getDeviceCapabilities().getExecutionEnvironment().put("leader", true);
            }
            devices.add(node);
        });
        // Run some cycles
        for (int i = 0; i < iterations; i++) {
            devices.forEach(Device::runCycle);
        }
        // Stop the server socket
        devices.forEach(d -> {
            try {
                ((MqttNetworkManager) d.getNetworkManager()).stop().waitForCompletion();
            } catch (MqttException e) {
                throw new IllegalStateException(e);
            }
        });
    }
}
