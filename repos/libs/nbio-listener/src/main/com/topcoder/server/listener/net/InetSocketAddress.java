package com.topcoder.server.listener.net;

import java.net.InetAddress;

/**
 * This class implements an IP socket address (IP address + port number).
 */
public class InetSocketAddress {

    private final int port;
    private final InetAddress address;

    /**
     * Creates a socket address from an IP address and a port number.
     *
     * @param   address     the IP address.
     * @param   port        the port number.
     */
    public InetSocketAddress(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }

    /**
     * Gets the port number.
     *
     * @return  the port number.
     */
    public int getPort() {
        return port;
    }

    /**
     * Gets the <code>InetAddress</code>.
     *
     * @return  the <code>InetAddress</code>.
     */
    public InetAddress getAddress() {
        return address;
    }

    /**
     * Constructs a string representation of this <code>InetSocketAddress</code>.
     *
     * @return  a string representation of this object.
     */
    public String toString() {
        return address + ":" + port;
    }

}
