package demo;

import com.google.common.annotations.VisibleForTesting;
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
import java.util.stream.IntStream;

import static org.mockito.Mockito.times;


@ExtendWith(MockitoExtension.class)
class JavaHelloTest {

    private static final String PROTELIS_MODULE_NAME = "hello";
    private static final int N = 5;
    private static final int ITERATIONS = 3;
    private static final int LEADER = 0;
    @SuppressWarnings("checkstyle:constantname")
    private static final List<Device> DEVICES = new ArrayList<>();
    @SuppressWarnings("checkstyle:constantname")
    private static final List<Speaker> SPEAKERS = new ArrayList<>();
    private static Graph<Device, DefaultEdge> graph;

    @BeforeAll
    @VisibleForTesting
    static void init() {
        graph = new DefaultUndirectedGraph<>(DefaultEdge.class);
        for (int i = 0; i < N; i++) {
            final ProtelisProgram program = ProtelisLoader.parse(PROTELIS_MODULE_NAME);
            final ConsoleSpeaker speaker = Mockito.spy(new ConsoleSpeaker());
            SPEAKERS.add(speaker);
            final Device device = new Device(program, i, new EmulatedNetworkManager(new IntDeviceUID(i)), speaker);
            DEVICES.add(device);
            graph.addVertex(device);
        }
        DEVICES.get(LEADER).getDeviceCapabilities().getExecutionEnvironment().put("leader", true);
        IntStream.range(0, DEVICES.size()).forEach(i -> graph.addEdge(DEVICES.get(i), DEVICES.get((i + 1) % DEVICES.size())));
        DEVICES.forEach(d -> ((EmulatedNetworkManager) d.getNetworkManager()).setNeighbors(Graphs.neighborSetOf(graph, d)));
        for (int i = 0; i < ITERATIONS; i++) {
            DEVICES.forEach(Device::runCycle);
        }
    }

    @Test
    @VisibleForTesting
    @DisplayName("The leader count should be correct")
    void testLeaderCount() {
        float c = 3f;
        for (int i = 0; i < ITERATIONS; i++) {
            Mockito.verify(SPEAKERS.get(LEADER)).announce("The leader's count is: " + c--);
        }
    }

    @Test
    @VisibleForTesting
    @DisplayName("The leader should be at " + LEADER)
    void testLeaderMessage() {
        Mockito.verify(SPEAKERS.get(LEADER), times(ITERATIONS)).announce("The leader is at 0");
    }

    @Test
    @VisibleForTesting
    @DisplayName("The leader neighbors should say something")
    void testNeighborsMessage() {
        final int prev = (LEADER + N - 1) % N;
        final int next = (LEADER + 1) % N;
        Mockito.verify(SPEAKERS.get(prev), times(ITERATIONS)).announce("Hello from the leader to its neighbor at " + prev);
        Mockito.verify(SPEAKERS.get(next), times(ITERATIONS)).announce("Hello from the leader to its neighbor at " + next);
    }
}
