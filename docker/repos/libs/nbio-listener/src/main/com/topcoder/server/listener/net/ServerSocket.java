package com.topcoder.server.listener.net;

import java.io.IOException;

import com.topcoder.server.listener.net.InetSocketAddress;

/**
 * This class implements server sockets.
 */
public abstract class ServerSocket {

    /**
     * Binds the <code>ServerSocket</code> to a specific address.
     *
     * @param   endpoint                the IP address and port number to bind to.
     * @param   backlog                 the listen backlog length.
     * @throws  java.io.IOException     if the bind operation fails, or if the socket is already bound.
     */
    public abstract void bind(InetSocketAddress endpoint, int backlog) throws IOException;

}
