import com.uchuhimo.konf.BaseConfig;
import com.uchuhimo.konf.Config;
import demo.*;
import demo.data.ProtelisNode;
import org.apache.commons.math3.util.Pair;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
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
class JavaSocketTest {

    private static int iterations;
    private static final List<Device> devices = new ArrayList<>();
    private static final List<Speaker> speakers = new ArrayList<>();
    private static Config config = new BaseConfig();
    private static List<ProtelisNode> nodes;
    private static List<Integer> leaders;


    @BeforeAll
    static void init() {
        config.addSpec(ProtelisConfigSpec.spec);
        config = config.from().toml.resource("config.toml");
        String protelisModuleName = config.get(ProtelisConfigSpec.protelisModuleName);
        iterations = config.get(ProtelisConfigSpec.iterations);
        nodes = config.get(ProtelisConfigSpec.nodes);
        leaders = config.get(ProtelisConfigSpec.nodes).stream()
                .filter(ProtelisNode::isLeader)
                .map(ProtelisNode::getId)
                .collect(Collectors.toList());
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
        for (int i = 0; i < iterations; i++) {
            devices.forEach(Device::runCycle);
        }
    }

    @Test
    @DisplayName("There should be at least 1 leader")
    void testAreThereLeaders() {
        assert(leaders.size() > 0);
    }

    @Test
    @DisplayName("The leader count should be correct")
    void testSocketLeaderCount() {
        List<String> messages = DoubleStream.iterate(3f, i -> i - 1)
                .limit(3)
                .mapToObj(x -> "The leader's count is: " + x)
                .collect(Collectors.toList());
        leaders.stream()
                .map(x -> Mockito.verify(speakers.get(x)))
                .flatMap(mock -> messages.stream().map(msg -> new Pair<>(mock, msg)))
                .forEach(x -> x.getFirst().announce(x.getSecond()));
        int leftovers = config.get(ProtelisConfigSpec.iterations) - 3;
        if (leftovers > 0) {
            leaders.forEach(x -> Mockito.verify(speakers.get(x), times(leftovers)).announce("The leader's count is: 0.0"));
        }
    }

    @Test
    @DisplayName("The leaders should print their id")
    void testSocketLeaderMessage() {
        leaders.stream()
                .forEach(x -> Mockito.verify(speakers.get(x), times(iterations)).announce("The leader is at " + x));
    }

    @Test
    @DisplayName("The leader neighbors should say something")
    void testSocketNeighborsMessage() {
        leaders.stream()
                .flatMap(x -> Arrays.asList((x + nodes.size() - 1) % nodes.size(), (x + 1) % nodes.size()).stream())
                .forEach(x -> Mockito.verify(speakers.get(x), atLeastOnce()).announce("Hello from the leader to its neighbor at " + x));
    }
}
