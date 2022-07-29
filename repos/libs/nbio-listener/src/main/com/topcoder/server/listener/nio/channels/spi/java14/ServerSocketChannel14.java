package com.topcoder.server.listener.nio.channels.spi.java14;

import java.io.IOException;

import com.topcoder.server.listener.net.ServerSocket;
import com.topcoder.server.listener.nio.channels.ClosedByInterruptException;
import com.topcoder.server.listener.nio.channels.ClosedChannelException;
import com.topcoder.server.listener.nio.channels.SelectableChannel;
import com.topcoder.server.listener.nio.channels.SelectionKey;
import com.topcoder.server.listener.nio.channels.Selector;
import com.topcoder.server.listener.nio.channels.ServerSocketChannel;
import com.topcoder.server.listener.nio.channels.SocketChannel;

final class ServerSocketChannel14 extends ServerSocketChannel {
    private final java.nio.channels.ServerSocketChannel channel;
    private final ServerSocket socket;
    
    ServerSocketChannel14() throws IOException {
        channel = java.nio.channels.ServerSocketChannel.open();
        socket = new ServerSocket14(channel.socket());
    }

    public ServerSocket socket() {
        return socket;
    }

    public SelectableChannel configureBlocking(boolean block) throws IOException {
        channel.configureBlocking(block);
        return this;
    }

    protected Object registerSpi(Selector sel, int ops, Object att) throws ClosedChannelException {
        try {
            return channel.register(((Selector14) sel).selector14(), ops, att);
        } catch (java.nio.channels.ClosedChannelException e) {
            throw new ClosedChannelException();
        }
    }

    protected SelectionKey newSelectionKey(Object key) {
        return new SelectionKey14((java.nio.channels.SelectionKey) key);
    }

    public SocketChannel accept() throws ClosedByInterruptException, IOException {
        try {
            return new SocketChannel14(channel.accept());
        } catch (java.nio.channels.ClosedByInterruptException e) {
            throw new ClosedByInterruptException();
        }
    }

    protected void closeSpi() throws IOException {
        channel.close();
    }

    protected void removeKey(Selector sel, Object key) {
    }

}
