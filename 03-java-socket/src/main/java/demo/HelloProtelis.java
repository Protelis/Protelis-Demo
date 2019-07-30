package demo;

import com.uchuhimo.konf.BaseConfig;
import com.uchuhimo.konf.Config;
import demo.data.ProtelisNode;
import org.protelis.lang.ProtelisLoader;
import org.protelis.vm.ProtelisProgram;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HelloProtelis {
    private static final List<Device> devices = new ArrayList<>();
    private static Config config = new BaseConfig();

    public static void main(String[] args) {
        config.addSpec(ProtelisConfigSpec.spec);
        config = config.from().toml.resource("config.toml");
        String protelisModuleName = config.get(ProtelisConfigSpec.protelisModuleName);
        int iterations = config.get(ProtelisConfigSpec.iterations);
        List<ProtelisNode> nodes = config.get(ProtelisConfigSpec.nodes);
        nodes.forEach(n -> {
            SocketNetworkManager netmgr = new SocketNetworkManager(new IntDeviceUID(n.getId()), n.getHostandport().getPort(), n.getNeighbors());
            try {
                netmgr.listen();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ProtelisProgram program = ProtelisLoader.parse(protelisModuleName);
            Device node = new Device(program, n.getId(), netmgr, new ConsoleSpeaker());
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
        devices.forEach(d -> ((SocketNetworkManager)d.getNetworkManager()).stop());
    }
}
