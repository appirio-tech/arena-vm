package com.topcoder.server.listener.nio.channels.spi;

import java.io.IOException;

import com.topcoder.server.listener.nio.channels.Selector;
import com.topcoder.server.listener.nio.channels.ServerSocketChannel;
import com.topcoder.server.listener.nio.channels.SocketChannel;

/**
 * Service-provider class for selectors and selectable channels.
 */
public abstract class SelectorProvider {

    private static SelectorProvider provider = new com.topcoder.server.listener.nio.channels.spi.java14.SelectorProvider14();

    /**
     * Initializes a new instance of this class.
     */
    protected SelectorProvider() {
    }

    /**
     * Returns the system-wide default selector provider for this invocation of the Java virtual machine.
     *
     * @return  the system-wide default selector provider.
     */
    public static final SelectorProvider provider() {
        return provider;
    }

    /**
     * Opens a socket channel.
     *
     * @return  the new channel.
     * @throws  java.io.IOException     if an I/O error occurs.
     */
    public abstract SocketChannel openSocketChannel() throws IOException;

    /**
     * Opens a server-socket channel.
     *
     * @return  the new channel.
     * @throws  java.io.IOException     if an I/O error occurs.
     */
    public abstract ServerSocketChannel openServerSocketChannel() throws IOException;

    /**
     * Opens a selector.
     *
     * @return  the new selector.
     * @throws  java.io.IOException     if an I/O error occurs.
     */
    public abstract Selector openSelector() throws IOException;
}
