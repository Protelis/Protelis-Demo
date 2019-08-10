package demo;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.protelis.lang.ProtelisLoader;
import org.protelis.vm.ProtelisProgram;

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
     */
    public static void main(final String[] args) {
        final String protelisModuleName = "hello";
        final int n = 5;
        final int iterations = 3;
        final List<Device> devices = new ArrayList<>();
        // Initialize a graph
        Graph<Device, DefaultEdge> g = new DefaultUndirectedGraph<>(DefaultEdge.class);
        // Initialize some devices
        for (int i = 0; i < n; i++) {
            ProtelisProgram program = ProtelisLoader.parse(protelisModuleName);
            Device d = new Device(program, i, new EmulatedNetworkManager(new IntDeviceUID(i)), new ConsoleSpeaker());
            devices.add(d);
            g.addVertex(d);
        }
        // Make the first one leader
        devices.get(0).getDeviceCapabilities().getExecutionEnvironment().put("leader", true);
        // Add the devices into the graph and link them as a ring network
        for (int i = 0; i < devices.size(); i++) {
            g.addEdge(devices.get(i), devices.get((i + 1) % devices.size()));
        }
        // Let the devices know the network topology
        devices.forEach(d -> ((EmulatedNetworkManager) d.getNetworkManager()).setNeighbors(Graphs.neighborSetOf(g, d)));
        // Let the devices execute 3 times
        for (int i = 0; i < iterations; i++) {
            devices.forEach(Device::runCycle);
        }
    }
}
