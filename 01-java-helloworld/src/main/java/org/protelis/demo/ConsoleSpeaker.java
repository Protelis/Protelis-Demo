package org.protelis.demo;

/**
 * Speaker implementation which uses the standard output.
 */
public class ConsoleSpeaker implements Speaker {
    /**
     * Prints the message to the standard output.
     * @param message
     */
    @Override
    public void announce(final String message) {
        System.out.println(message); // NOPMD: println used by purpose
    }
}
