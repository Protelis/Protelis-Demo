package demo;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.protelis.lang.ProtelisLoader;
import org.protelis.vm.ProtelisProgram;

import java.util.ArrayList;
import java.util.List;

public class HelloProtelis {

    private final static String protelisModuleName = "hello";
    private final static int N = 5;
    private final static int iterations = 3;
    private final static List<Device> devices = new ArrayList<>();

    public static void main(String[] args) {
        // Initialize a graph
        Graph<Device, DefaultEdge> g = new DefaultUndirectedGraph<>(DefaultEdge.class);
        // Initialize some devices
        for (int i = 0; i < N; i++) {
            ProtelisProgram program = ProtelisLoader.parse(protelisModuleName);
            Device d = new Device(program, i, new EmulatedNetworkManager(new IntDeviceUID(i)));
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
        devices.forEach(d -> ((EmulatedNetworkManager)d.getNetworkManager()).setNeighbors(Graphs.neighborSetOf(g, d)));
        // Let the devices execute 3 times
        for (int i = 0; i < iterations; i++) {
            devices.forEach(Device::runCycle);
            devices.forEach(Device::sendMessages);
        }
    }
}
