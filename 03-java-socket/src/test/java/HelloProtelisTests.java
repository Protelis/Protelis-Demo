import com.uchuhimo.konf.BaseConfig;
import com.uchuhimo.konf.Config;
import demo.*;
import demo.data.ProtelisNode;
import org.apache.commons.math3.util.Pair;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.protelis.lang.ProtelisLoader;
import org.protelis.vm.ProtelisProgram;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class HelloProtelisTests {

    private static int iterations;
    private static final List<Device> devices = new ArrayList<>();
    private static final List<Speaker> speakers = new ArrayList<>();
    private static Config config = new BaseConfig();
    private static List<ProtelisNode> nodes;


    @BeforeAll
    static void init() {
        config.addSpec(ProtelisConfigSpec.spec);
        config = config.from().toml.resource("config.toml");
        String protelisModuleName = config.get(ProtelisConfigSpec.protelisModuleName);
        iterations = config.get(ProtelisConfigSpec.iterations);
        nodes = config.get(ProtelisConfigSpec.nodes);
        nodes.forEach(n -> {
            SocketNetworkManager netmgr = new SocketNetworkManager(new IntDeviceUID(n.getId()), n.getHostandport().getPort(), n.getNeighbors());
            try {
                netmgr.listen();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ProtelisProgram program = ProtelisLoader.parse(protelisModuleName);
            Speaker speaker = Mockito.spy(new ConsoleSpeaker());
            speakers.add(speaker);
            Device node = new Device(program, n.getId(), netmgr, speaker);
            if (n.isLeader()) {
                node.getDeviceCapabilities().getExecutionEnvironment().put("leader", true);
            }
            devices.add(node);
        });
    }

    @Test
    void testExecution() {
        List<Integer> leaders = config.get(ProtelisConfigSpec.nodes).stream()
                .filter(ProtelisNode::isLeader)
                .map(ProtelisNode::getId)
                .collect(Collectors.toList());
        for (int i = 0; i < iterations; i++) {
            devices.forEach(Device::runCycle);
        }

        List<String> messages = DoubleStream.iterate(3f, i -> i - 1)
                .limit(iterations)
                .mapToObj(x -> "The leader's count is: " + x)
                .collect(Collectors.toList());

        leaders.stream()
                .map(x -> Mockito.verify(speakers.get(x)))
                .flatMap(mock -> messages.stream().map(msg -> new Pair<>(mock, msg)))
                .forEach(x -> x.getFirst().announce(x.getSecond()));

        leaders.stream()
                .map(x -> new Pair<>(Mockito.verify(speakers.get(x), times(iterations)),
                        "The leader is at " + x))
                .forEach(x -> (x.getFirst()).announce(x.getSecond()));

        leaders.stream()
                .map(x -> Arrays.asList((x + nodes.size() - 1) % nodes.size(), (x + 1) % nodes.size()))
                .flatMap(Collection::stream)
                .map(x -> new Pair<>(Mockito.verify(speakers.get(x), atLeastOnce()),
                        "Hello from the leader to its neighbor at " + x))
                .forEach(x -> x.getFirst().announce(x.getSecond()));
    }
}
