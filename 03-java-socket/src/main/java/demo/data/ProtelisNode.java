package demo.data;

import com.google.common.collect.ImmutableSet;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.beans.ConstructorProperties;
import java.util.Set;

/**
 * POJO class representing a Protelis node.
 */
public final class ProtelisNode {
    private final IPv4Host hostandport;
    private final int id;
    private final ImmutableSet<IPv4Host> neighbors;
    private final boolean leader;

    /**
     * Constructor method.
     * @param hostandport the host and port of the node
     * @param id the unique id of the node
     * @param neighbors the neighbors of the node
     * @param leader whether the node is a leader or not
     */
    @ConstructorProperties({"hostandport", "id", "neighbors", "leader"})
    public ProtelisNode(final IPv4Host hostandport, final int id, final Set<IPv4Host> neighbors, final boolean leader) {
        this.hostandport = hostandport;
        this.id = id;
        this.neighbors = ImmutableSet.copyOf(neighbors);
        this.leader = leader;
    }

    /**
     * Constructor method with no leader property.
     * @param hostandport the host and port of the node
     * @param id the unique id of the node
     * @param neighbors the neighbors of the node
     */

    public ProtelisNode(final IPv4Host hostandport, final int id, final Set<IPv4Host> neighbors) {
        this(hostandport, id, neighbors, false);
    }

    /**
     * Getter for the host and port.
     * @return the host and port of the node
     */
    public IPv4Host getHostandport() {
        return hostandport;
    }

    /**
     * Getter for the id.
     * @return the id of the node.
     */
    public int getId() {
        return id;
    }

    /**
     * Getter for the neighbors of the node.
     * @return the neighbors of the node.
     */
    @SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "The field is immutable")
    public Set<IPv4Host> getNeighbors() {
        return neighbors;
    }

    /**
     * Checks if the node is leader or not.
     * @return true if the node is leader
     */
    public boolean isLeader() {
        return leader;
    }
}
