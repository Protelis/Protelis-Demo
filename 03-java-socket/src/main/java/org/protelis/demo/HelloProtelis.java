package org.protelis.demo;

import com.uchuhimo.konf.BaseConfig;
import com.uchuhimo.konf.Config;
import com.uchuhimo.konf.source.DefaultLoaders;
import com.uchuhimo.konf.source.DefaultTomlLoaderKt;
import com.uchuhimo.konf.source.Loader;
import org.protelis.demo.data.ProtelisNode;
import org.protelis.lang.ProtelisLoader;
import org.protelis.vm.ProtelisProgram;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Example usage of the implemented classes.
 */
public final class HelloProtelis {

    private HelloProtelis() { }

    /**
     * Main method.
     * @param args unused
     * @throws IOException in case of issues with local networking
     */
    public static void main(final String[] args) throws IOException {
        Config config = new BaseConfig();
        config.addSpec(ProtelisConfigSpec.SPEC);
        final DefaultLoaders defaultLoaders = config.from();
        final Loader toml = DefaultTomlLoaderKt.getToml(defaultLoaders);
        config = toml.resource("config.toml", false);
        final String protelisModuleName = config.get(ProtelisConfigSpec.protelisModuleName);
        final int iterations = config.get(ProtelisConfigSpec.iterations);
        final List<ProtelisNode> nodes = config.get(ProtelisConfigSpec.nodes);
        final List<Device> devices = new ArrayList<>();
        for (final ProtelisNode n: nodes) {
            final SocketNetworkManager netmgr = new SocketNetworkManager(
                new IntDeviceUID(n.getId()),
                n.getHostandport().getPort(),
                n.getNeighbors()
            );
            netmgr.listen();
            final ProtelisProgram program = ProtelisLoader.parse(protelisModuleName);
            final Device node = new Device(program, n.getId(), netmgr, new ConsoleSpeaker());
            if (n.isLeader()) {
                node.getDeviceCapabilities().getExecutionEnvironment().put("leader", true);
            }
            devices.add(node);
        }
        // Run some cycles
        for (int i = 0; i < iterations; i++) {
            devices.forEach(Device::runCycle);
        }
        // Stop the server socket
        devices.forEach(d -> ((SocketNetworkManager) d.getNetworkManager()).stop());
    }
}
