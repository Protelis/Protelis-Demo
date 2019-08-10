import demo.ConsoleSpeaker;
import demo.Device;
import demo.EmulatedNetworkManager;
import demo.IntDeviceUID;
import demo.Speaker;
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
class JavaHelloTest {

    private static final String PROTELIS_MODULE_NAME = "hello";
    private static final int N = 5;
    private static final int ITERATIONS = 3;
    private static final int LEADER = 0;
    @SuppressWarnings("checkstyle:constantname")
    private static final List<Device> devices = new ArrayList<>();
    @SuppressWarnings("checkstyle:constantname")
    private static final List<Speaker> speakers = new ArrayList<>();

    @BeforeAll
    static void init() {
        Graph<Device, DefaultEdge> g = new DefaultUndirectedGraph<>(DefaultEdge.class);
        for (int i = 0; i < N; i++) {
            ProtelisProgram program = ProtelisLoader.parse(PROTELIS_MODULE_NAME);
            ConsoleSpeaker speaker = Mockito.spy(new ConsoleSpeaker());
            speakers.add(speaker);
            Device device = new Device(program, i, new EmulatedNetworkManager(new IntDeviceUID(i)), speaker);
            devices.add(device);
            g.addVertex(device);
        }
        devices.get(LEADER).getDeviceCapabilities().getExecutionEnvironment().put("leader", true);
        for (int i = 0; i < devices.size(); i++) {
            g.addEdge(devices.get(i), devices.get((i + 1) % devices.size()));
        }
        devices.forEach(d -> ((EmulatedNetworkManager) d.getNetworkManager()).setNeighbors(Graphs.neighborSetOf(g, d)));
        for (int i = 0; i < ITERATIONS; i++) {
            devices.forEach(Device::runCycle);
        }
    }

    @Test
    @DisplayName("The leader count should be correct")
    void testLeaderCount() {
        float c = 3f;
        for (int i = 0; i < ITERATIONS; i++) {
            Mockito.verify(speakers.get(LEADER)).announce("The leader's count is: " + c--);
        }
    }

    @Test
    @DisplayName("The leader should be at " + LEADER)
    void testLeaderMessage() {
        Mockito.verify(speakers.get(LEADER), times(ITERATIONS)).announce("The leader is at 0");
    }

    @Test
    @DisplayName("The leader neighbors should say something")
    void testNeighborsMessage() {
        int prev = (LEADER + N - 1) % N;
        int next = (LEADER + 1) % N;
        Mockito.verify(speakers.get(prev), times(ITERATIONS)).announce("Hello from the leader to its neighbor at " + prev);
        Mockito.verify(speakers.get(next), times(ITERATIONS)).announce("Hello from the leader to its neighbor at " + next);
    }
}
