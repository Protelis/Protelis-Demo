package demo;

import com.google.common.annotations.VisibleForTesting;
import com.uchuhimo.konf.BaseConfig;
import com.uchuhimo.konf.Config;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class JavaSocketTest {

    private static int iterations;
    private static final List<Device> DEVICES = new ArrayList<>();
    private static final List<Speaker> SPEAKERS = new ArrayList<>();
    private static Config config = new BaseConfig();
    private static List<ProtelisNode> nodes;
    private static List<Integer> leaders;


    @BeforeAll
    @VisibleForTesting
    static void init() {
        config.addSpec(ProtelisConfigSpec.SPEC);
        config = config.from().toml.resource("config.toml");
        final String protelisModuleName = config.get(ProtelisConfigSpec.protelisModuleName);
        iterations = config.get(ProtelisConfigSpec.iterations);
        nodes = config.get(ProtelisConfigSpec.nodes);
        leaders = config.get(ProtelisConfigSpec.nodes).stream()
                .filter(ProtelisNode::isLeader)
                .map(ProtelisNode::getId)
                .collect(Collectors.toList());
        nodes.forEach(n -> {
            final SocketNetworkManager netmgr = new SocketNetworkManager(new IntDeviceUID(n.getId()), n.getHostandport().getPort(), n.getNeighbors());
            try {
                netmgr.listen();
            } catch (IOException e) {
                e.printStackTrace();
            }
            final ProtelisProgram program = ProtelisLoader.parse(protelisModuleName);
            final Speaker speaker = Mockito.spy(new ConsoleSpeaker());
            SPEAKERS.add(speaker);
            final Device node = new Device(program, n.getId(), netmgr, speaker);
            if (n.isLeader()) {
                node.getDeviceCapabilities().getExecutionEnvironment().put("leader", true);
            }
            DEVICES.add(node);
        });
        for (int i = 0; i < iterations; i++) {
            DEVICES.forEach(Device::runCycle);
        }
    }

    @Test
    @VisibleForTesting
    @DisplayName("There should be at least 1 leader")
    void testAreThereLeaders() {
        assert !leaders.isEmpty();
    }

    @Test
    @VisibleForTesting
    @DisplayName("The leader count should be correct")
    void testSocketLeaderCount() {
        final List<String> messages = DoubleStream.iterate(3f, i -> i - 1)
                .limit(3)
                .mapToObj(x -> "The leader's count is: " + x)
                .collect(Collectors.toList());
        leaders.stream()
                .map(x -> Mockito.verify(SPEAKERS.get(x)))
                .flatMap(mock -> messages.stream().map(msg -> new Pair<>(mock, msg)))
                .forEach(x -> x.getFirst().announce(x.getSecond()));
        final int leftovers = config.get(ProtelisConfigSpec.iterations) - 3;
        if (leftovers > 0) {
            leaders.forEach(x -> Mockito.verify(SPEAKERS.get(x), times(leftovers)).announce("The leader's count is: 0.0"));
        }
    }

    @Test
    @VisibleForTesting
    @DisplayName("The leaders should print their id")
    void testSocketLeaderMessage() {
        leaders.stream()
                .forEach(x -> Mockito.verify(SPEAKERS.get(x), times(iterations)).announce("The leader is at " + x));
    }

    @Test
    @VisibleForTesting
    @DisplayName("The leader neighbors should say something")
    void testSocketNeighborsMessage() {
        leaders.stream()
                .flatMap(x -> Arrays.asList((x + nodes.size() - 1) % nodes.size(), (x + 1) % nodes.size()).stream())
                .forEach(x -> Mockito.verify(SPEAKERS.get(x), atLeastOnce()).announce("Hello from the leader to its neighbor at " + x));
    }
}
