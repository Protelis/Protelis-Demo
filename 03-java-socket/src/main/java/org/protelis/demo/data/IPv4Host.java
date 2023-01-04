package org.protelis.demo.data;

import java.beans.ConstructorProperties;

/**
 * POJO class representing an IPv4 socket.
 */
public final class IPv4Host {

    private final String host;
    private final int port;

    /**
     * Constructor method.
     * @param host the hostname
     * @param port the port
     */
    @ConstructorProperties({"host", "port"})
    public IPv4Host(final String host, final int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * Getter for the port.
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * Getter for the hostname.
     * @return the hostname
     */
    public String getHost() {
        return host;
    }
}
