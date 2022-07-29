package com.topcoder.server.listener.nio.channels;

import java.io.IOException;
import java.net.Socket;

import com.topcoder.server.listener.net.InetSocketAddress;
import com.topcoder.server.listener.nio.channels.spi.SelectorProvider;

/**
 * A selectable channel for stream-oriented connecting sockets.
 */
public abstract class SocketChannel extends SelectableChannel {

    /**
     * Initializes a new instance of this class.
     */
    protected SocketChannel() {
    }

    /**
     * Opens a socket channel.
     *
     * @return  a new socket channel.
     * @throws  java.io.IOException         if an I/O error occurs.
     */
    public static final SocketChannel open() throws IOException {
        return SelectorProvider.provider().openSocketChannel();
    }

    /**
     * Opens a socket channel and connects it to a remote address.
     *
     * @param   remote                      the remote address to which this channel is to be connected.
     * @return  a new socket channel.
     * @throws  java.io.IOException         if an I/O error occurs.
     */
    public static final SocketChannel open(InetSocketAddress remote) throws IOException {
        SocketChannel channel = open();
        channel.connect(remote);
        return channel;
    }

    /**
     * Connects this channel's socket.
     *
     * @param   remote      the remote address to which this channel is to be connected.
     * @return  <code>true</code> if a connection was established, <code>false</code> if this channel is
     *          in non-blocking mode and the connection operation is in progress.
     * @throws  java.io.IOException         if an I/O error occurs.
     */
    protected abstract boolean connect(InetSocketAddress remote) throws IOException;

    /**
     * Retrieves a socket associated with this channel.
     *
     * @return  a socket associated with this channel.
     */
    public abstract Socket socket();

    /**
     * Reads a sequence of bytes from this channel into the given buffer.
     *
     * @param   dst                         the buffer into which bytes are to be transferred.
     * @return  the number of bytes read, possibly zero, or -1 if the channel has reached end-of-stream.
     * @throws  ClosedByInterruptException  if another thread interrupts the current thread while the accept operation
     *                                      is in progress, thereby closing the channel and setting the current
     *                                      thread's interrupt status.
     * @throws  ClosedChannelException      if this channel is closed.
     * @throws  java.io.IOException         if an I/O error occurs.
     */
    public abstract int read(java.nio.ByteBuffer dst) throws ClosedByInterruptException, ClosedChannelException, IOException;

    /**
     * Writes a sequence of bytes to this channel from the given buffer.
     *
     * @param   src                         the buffer from which bytes are to be retrieved.
     * @return  the number of bytes written, possibly zero.
     * @throws  ClosedChannelException      if this channel is closed.
     * @throws  java.io.IOException         if an I/O error occurs.
     */
    public abstract int write(java.nio.ByteBuffer src) throws ClosedChannelException, IOException;

}
