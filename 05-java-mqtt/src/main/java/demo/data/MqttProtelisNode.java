package demo.data;

import com.google.common.collect.ImmutableList;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.beans.ConstructorProperties;
import java.util.List;

/**
 * POJO class representing a Protelis node.
 */
public final class MqttProtelisNode {
    private final int id;
    private final String listen;
    private final ImmutableList<String> neighbors;
    private final boolean leader;

    /**
     * Constructor method.
     *
     * @param id        the unique id of the node
     * @param listen    the topic listened by the node
     * @param neighbors the neighbors of the node
     * @param leader    whether the node is a leader or not
     */
    @ConstructorProperties({"id", "listen", "neighbors", "leader"})
    public MqttProtelisNode(final int id, final String listen, final List<String> neighbors, final boolean leader) {
        this.id = id;
        this.listen = listen;
        this.neighbors = ImmutableList.copyOf(neighbors);
        this.leader = leader;
    }

    /**
     * Constructor method with no leader property.
     *
     * @param id        the unique id of the node
     * @param listen    the topic listened by the node
     * @param neighbors the neighbors of the node
     */
    public MqttProtelisNode(final int id, final String listen, final List<String> neighbors) {
        this(id, listen, neighbors, false);
    }

    /**
     * Getter for the id.
     *
     * @return the id of the node.
     */
    public int getId() {
        return id;
    }

    /**
     * Getter for the listened topic.
     *
     * @return the listened topic.
     */
    public String getListen() {
        return listen;
    }

    /**
     * Getter for the neighbors of the node.
     *
     * @return the neighbors of the node.
     */
    @SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "The field is immutable")
    public List<String> getNeighbors() {
        return neighbors;
    }

    /**
     * Checks if the node is leader or not.
     *
     * @return true if the node is leader
     */
    public boolean isLeader() {
        return leader;
    }
}
