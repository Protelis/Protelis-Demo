import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.protelis.lang.ProtelisLoader;
import org.protelis.vm.ProtelisProgram;
import org.protelis.vm.util.CodePath;

/**
 * Minimal demonstration of an application using Protelis.
 * This demonstration does the following:
 * - Uses ProtelisLoader to obtain a program from the Protelis classes
 * - Create a collection of Protelis-based devices, each encapsulating a 
 *   ProtelisVM, execution environment, and network interface
 * - Run several rounds of synchronous execution
 */
public class HelloMain {
	/** Collection of devices */
	private static List<SimpleDevice> devices = new ArrayList<>();
	/** Network for moving messages between devices */
	private static Map<SimpleDevice,Set<SimpleDevice>> network = new HashMap<>();
	
	/** Kludged output to either standard out or a string */
	public static PrintStream out = System.out;
	public static ByteArrayOutputStream outBuffer = null;
	
	/**
	 * Entry point for executing this demonstration
	 */
	public static void main(String[] args) {
		// if the first argument is "string", then log to a string; otherwise, log to standard out
		if(args.length>0 && args[0].equals("string")) {
			outBuffer = new ByteArrayOutputStream();
			out = new PrintStream(outBuffer);
		}
		
		// Create the network of devices and connections
		out.println("Creating grid network");
		createNetwork("hello");
		
		// Run several synchronous rounds of execution
		for(int i=0;i<3;i++) {
			out.println("Executing round "+i);
			synchronousUpdate();
		}
		
		// All done!
		out.println("Finished executing");
	}
	
	/**
	 * Create an N x N grid of devices, each running the indicated program.
	 * @param program
	 */
	private static void createNetwork(String protelisModuleName) {
		// Make a square grid of EDGE_LENGTH x EDGE_LENGTH devices
		final int EDGE_LENGTH = 4;
		// One of these devices will be marked in its environment as a leader
		final int LEADER_ID = 5; // should have neighbors 1, 4, 6, 9

		SimpleDevice[][] cache = new SimpleDevice[EDGE_LENGTH][EDGE_LENGTH];
		// Create devices
		for(int i=0;i<EDGE_LENGTH;i++) {
			for(int j=0;j<EDGE_LENGTH;j++) {
				int id = i*4+j;
				// Parse a new copy of the program for each device:
				// it will be marked up with values as the interpreter runs
				ProtelisProgram program = ProtelisLoader.parse(protelisModuleName);
				// Create the device
				SimpleDevice executionContext = new SimpleDevice(program,id);
				devices.add(executionContext);
				// Mark the leader
				if(id==LEADER_ID) { 
					executionContext.getExecutionEnvironment().put("leader", true);
				}
				// Remember the devices in a grid, for later setting up the network
				cache[i][j] = executionContext;
			}
		}
		
		// Link them together to form a Manhattan grid network
		for(int i=0;i<EDGE_LENGTH;i++) {
			for(int j=0;j<EDGE_LENGTH;j++) {
				SimpleDevice dev = cache[i][j];
				Set<SimpleDevice> neighbors = new HashSet<>();
				if(i>0) { neighbors.add(cache[i-1][j]); }
				if(i<(EDGE_LENGTH-1)) { neighbors.add(cache[i+1][j]); }
				if(j>0) { neighbors.add(cache[i][j-1]); }
				if(j<(EDGE_LENGTH-1)) { neighbors.add(cache[i][j+1]); }
				network.put(dev, neighbors);
			}
		}
	}

	/**
	 * Execute every device once, then deliver updates to all neighbors.
	 */
	private static void synchronousUpdate() {
		// Execute one cycle at each device
		for(SimpleDevice d : devices) {
			d.getVM().runCycle();
		}
		
		// Deliver shared-state updates over the network
		for(SimpleDevice src : network.keySet()) {
			Map<CodePath,Object> message = src.accessNetworkManager().getSendCache();
			for(SimpleDevice dst : network.get(src)) {
				dst.accessNetworkManager().receiveFromNeighbor(src.getDeviceUID(),message);
			}
		}
	}
}
