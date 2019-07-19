package demo.data;

import java.beans.ConstructorProperties;

public final class IPv4Host {
    private final String host;
    private final int port;

    @ConstructorProperties({"host", "port"})
    public IPv4Host(final String host, final int port) {
        this.host = host;
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }
}
