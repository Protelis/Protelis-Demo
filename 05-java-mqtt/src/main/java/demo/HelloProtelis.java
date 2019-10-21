package demo;

import com.uchuhimo.konf.BaseConfig;
import com.uchuhimo.konf.Config;
import demo.data.MqttProtelisNode;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.protelis.lang.ProtelisLoader;
import org.protelis.vm.ProtelisProgram;

import java.util.ArrayList;
import java.util.List;

public final class HelloProtelis {

    private HelloProtelis() { }

    /**
     * Main method.
     * @param args unused.
     */
    public static void main(final String[] args) {
        Config config = new BaseConfig();
        config.addSpec(ProtelisConfigSpec.SPEC);
        config = config.from().toml.resource("config.toml");
        final String protelisModuleName = config.get(ProtelisConfigSpec.protelisModuleName);
        final int iterations = config.get(ProtelisConfigSpec.iterations);
        final List<MqttProtelisNode> nodes = config.get(ProtelisConfigSpec.nodes);
        final List<Device> devices = new ArrayList<>();

        nodes.forEach(n -> {
            final MqttNetworkManager netmgr = new MqttNetworkManager(new IntDeviceUID(n.getId()), n.getNeighbors());
            try {
                netmgr.listen(n.getListen()).waitForCompletion();
            } catch (MqttException e) {
                e.printStackTrace();
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
                e.printStackTrace();
            }
        });
    }
}
