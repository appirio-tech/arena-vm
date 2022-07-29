package com.topcoder.server.listener.nio.channels.spi.java14;

import java.io.IOException;
import java.net.Socket;

import com.topcoder.server.listener.net.InetSocketAddress;
import com.topcoder.server.listener.nio.channels.ClosedByInterruptException;
import com.topcoder.server.listener.nio.channels.ClosedChannelException;
import com.topcoder.server.listener.nio.channels.SelectableChannel;
import com.topcoder.server.listener.nio.channels.SelectionKey;
import com.topcoder.server.listener.nio.channels.Selector;
import com.topcoder.server.listener.nio.channels.SocketChannel;

final class SocketChannel14 extends SocketChannel {

    private final java.nio.channels.SocketChannel channel;

    SocketChannel14() throws IOException {
        this(java.nio.channels.SocketChannel.open());
    }

    SocketChannel14(java.nio.channels.SocketChannel channel) {
        this.channel = channel;
    }

    public boolean connect(InetSocketAddress remote) throws IOException {
        return channel.connect(ServerSocket14.getAddress(remote));
    }

    public Socket socket() {
        return channel.socket();
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

    public int read(java.nio.ByteBuffer dst) throws ClosedByInterruptException, ClosedChannelException, IOException {
        try {
            return channel.read(dst);
        } catch (java.nio.channels.ClosedByInterruptException e) {
            throw new ClosedByInterruptException();
        } catch (java.nio.channels.ClosedChannelException e) {
            throw new ClosedChannelException();
        }
    }

    public int write(java.nio.ByteBuffer src) throws ClosedChannelException, IOException {
        try {
            return channel.write(src);
        } catch (java.nio.channels.ClosedChannelException e) {
            throw new ClosedChannelException();
        }
    }

    protected void closeSpi() throws IOException {
        channel.close();
    }

    protected void removeKey(Selector sel, Object key) {
    }

}
