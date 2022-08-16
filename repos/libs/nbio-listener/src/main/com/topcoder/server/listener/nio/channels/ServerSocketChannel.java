package com.topcoder.server.listener.nio.channels;

import java.io.IOException;

import com.topcoder.server.listener.net.ServerSocket;
import com.topcoder.server.listener.nio.channels.spi.SelectorProvider;

/**
 * A selectable channel for stream-oriented listening sockets.
 */
public abstract class ServerSocketChannel extends SelectableChannel {

    /**
     * Initializes a new instance of this class.
     */
    protected ServerSocketChannel() {
    }

    /**
     * Opens a server-socket channel.
     *
     * @return  a new socket channel.
     * @throws  java.io.IOException     if an I/O error occurs.
     */
    public static final ServerSocketChannel open() throws IOException {
        return SelectorProvider.provider().openServerSocketChannel();
    }

    /**
     * Retrieves a server socket associated with this channel.
     *
     * @return  a server socket associated with this channel.
     */
    public abstract ServerSocket socket();

    /**
     * Accepts a connection made to this channel's socket.
     *
     * @return  the socket channel for the new connection.
     * @throws  ClosedByInterruptException  if another thread interrupts the current thread while the accept operation
     *                                      is in progress, thereby closing the channel and setting the current
     *                                      thread's interrupt status.
     * @throws  java.io.IOException         if an I/O error occurs.
     */
    public abstract SocketChannel accept() throws ClosedByInterruptException, IOException;

}
