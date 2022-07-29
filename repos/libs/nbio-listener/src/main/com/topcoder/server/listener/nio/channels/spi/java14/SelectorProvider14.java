package com.topcoder.server.listener.nio.channels.spi.java14;

import java.io.IOException;

import com.topcoder.server.listener.nio.channels.Selector;
import com.topcoder.server.listener.nio.channels.ServerSocketChannel;
import com.topcoder.server.listener.nio.channels.SocketChannel;
import com.topcoder.server.listener.nio.channels.spi.SelectorProvider;

/**
 * Java 1.4 service-provider class.
 *
 * @author  Timur Zambalayev
 */
public final class SelectorProvider14 extends SelectorProvider {

    /**
     * Initializes a new instance of this class.
     */
    public SelectorProvider14() {
    }

    public ServerSocketChannel openServerSocketChannel() throws IOException {
        return new ServerSocketChannel14();
    }

    public SocketChannel openSocketChannel() throws IOException {
        return new SocketChannel14();
    }

    public Selector openSelector() throws IOException {
        return new Selector14();
    }

}
