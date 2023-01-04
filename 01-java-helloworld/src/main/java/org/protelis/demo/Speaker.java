package org.protelis.demo;

/**
 * Protelis component which allows a node to communicate.
 */
public interface Speaker {
    /**
     * Outputs a message.
     * @param message the message to announce.
     */
    void announce(String message);
}
