package demo.data;

import java.beans.ConstructorProperties;
import java.util.Set;

public final class ProtelisNode {
    private final IPv4Host hostandport;
    private final int id;
    private final Set<IPv4Host> neighbors;
    private final boolean leader;

    @ConstructorProperties({"hostandport", "id", "neighbors", "leader"})
    public ProtelisNode(IPv4Host hostandport, int id, Set<IPv4Host> neighbors, boolean leader) {
        this.hostandport = hostandport;
        this.id = id;
        this.neighbors = neighbors;
        this.leader = leader;
    }

    public ProtelisNode(IPv4Host hostandport, int id, Set<IPv4Host> neighbors) {
        this(hostandport, id, neighbors, false);
    }

    public IPv4Host getHostandport() {
        return hostandport;
    }

    public int getId() {
        return id;
    }

    public Set<IPv4Host> getNeighbors() {
        return neighbors;
    }

    public boolean isLeader() {
        return leader;
    }
}
