import demo.*;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.protelis.lang.ProtelisLoader;
import org.protelis.vm.ProtelisProgram;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class HelloProtelisTests {

    private final static String protelisModuleName = "hello";
    private final static int N = 5;
    private final static int iterations = 3;
    private final static int leader = 0;
    private final static List<Device> devices = new ArrayList<>();
    private final static List<Speaker> speakers = new ArrayList<>();

    @BeforeAll
    static void init() {
        Graph<Device, DefaultEdge> g = new DefaultUndirectedGraph<>(DefaultEdge.class);
        for (int i = 0; i < N; i++) {
            ProtelisProgram program = ProtelisLoader.parse(protelisModuleName);
            ConsoleSpeaker speaker = Mockito.spy(new ConsoleSpeaker());
            speakers.add(speaker);
            Device device = new Device(program, i, new EmulatedNetworkManager(new IntDeviceUID(i)), speaker);
            devices.add(device);
            g.addVertex(device);
        }
        devices.get(leader).getDeviceCapabilities().getExecutionEnvironment().put("leader", true);
        for (int i = 0; i < devices.size(); i++) {
            g.addEdge(devices.get(i), devices.get((i + 1) % devices.size()));
        }
        devices.forEach(d -> ((EmulatedNetworkManager)d.getNetworkManager()).setNeighbors(Graphs.neighborSetOf(g, d)));
        for (int i = 0; i < iterations; i++) {
            devices.forEach(Device::runCycle);
        }
    }

    @Test
    @DisplayName("The leader count should be correct")
    void testLeaderCount() {
        float c = 3f;
        for (int i = 0; i < iterations; i++) {
            Mockito.verify(speakers.get(leader)).announce("The leader's count is: " + c--);
        }
    }

    @Test
    @DisplayName("The leader should be at " + leader)
    void testLeaderMessage() {
        Mockito.verify(speakers.get(leader), times(iterations)).announce("The leader is at 0");
    }

    @Test
    @DisplayName("The leader neighbors should say something")
    void testNeighborsMessage() {
        int prev = (leader + N - 1) % N;
        int next = (leader + 1) % N;
        Mockito.verify(speakers.get(prev), times(iterations)).announce("Hello from the leader to its neighbor at " + prev);
        Mockito.verify(speakers.get(next), times(iterations)).announce("Hello from the leader to its neighbor at " + next);
    }
}
