package com.topcoder.server.listener.nio.channels;

import java.io.IOException;
import java.net.Socket;

import com.topcoder.server.listener.net.InetSocketAddress;
import com.topcoder.server.listener.nio.channels.SelectionKey;
import com.topcoder.server.listener.nio.channels.Selector;
import com.topcoder.server.listener.nio.channels.ServerSocketChannel;

import junit.framework.TestCase;

import com.topcoder.server.listener.AcceptHandlerTest;

public final class SelectionKeyTest extends TestCase {

    private static final int PORT = AcceptHandlerTest.PORT + 400;
    private static final int BACKLOG = 511;

    public SelectionKeyTest(String name) {
        super(name);
    }

    public void testCloseServerSocketChannel() {
        int port = PORT;
        ServerSocketChannel serverSocketChannel = null;
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            InetSocketAddress address = new InetSocketAddress(AcceptHandlerTest.HOST, port);
            serverSocketChannel.socket().bind(address, BACKLOG);
        } catch (IOException e) {
            fail();
        }
        Selector acceptSelector = null;
        try {
            acceptSelector = Selector.open();
            assertEquals(0, SelectionKey.getMapSize());
            serverSocketChannel.register(acceptSelector, SelectionKey.OP_ACCEPT);
            assertEquals(1, SelectionKey.getMapSize());
        } catch (IOException e) {
            fail();
        }
        try {
            acceptSelector.close();
            serverSocketChannel.close();
            assertEquals(0, SelectionKey.getMapSize());
        } catch (IOException e) {
            fail();
        }
    }

    public void testCloseSocketChannel() {
        int port = PORT + 1;
        ServerSocketChannel serverSocketChannel = null;
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            InetSocketAddress address = new InetSocketAddress(AcceptHandlerTest.HOST, port);
            serverSocketChannel.socket().bind(address, BACKLOG);
        } catch (IOException e) {
            fail();
        }
        Selector acceptSelector = null;
        try {
            acceptSelector = Selector.open();
            assertEquals(0, SelectionKey.getMapSize());
            serverSocketChannel.register(acceptSelector, SelectionKey.OP_ACCEPT);
            assertEquals(1, SelectionKey.getMapSize());
        } catch (IOException e) {
            fail();
        }
        Socket socket = null;
        try {
            socket = new Socket(AcceptHandlerTest.HOST, port);
        } catch (IOException e) {
            fail();
        }
        SocketChannel socketChannel = null;
        try {
            socketChannel = serverSocketChannel.accept();
            socketChannel.configureBlocking(false);
        } catch (IOException e) {
            fail();
        }
        Selector readSelector = null;
        try {
            readSelector = Selector.open();
            socketChannel.register(readSelector, SelectionKey.OP_READ);
            assertEquals(2, SelectionKey.getMapSize());
        } catch (IOException e) {
            fail();
        }
        Selector writeSelector = null;
        try {
            writeSelector = Selector.open();
            socketChannel.register(writeSelector, SelectionKey.OP_WRITE);
            assertEquals(3, SelectionKey.getMapSize());
        } catch (IOException e) {
            fail();
        }
        try {
            writeSelector.close();
            readSelector.close();
            socketChannel.close();
            assertEquals(1, SelectionKey.getMapSize());
            socket.close();
            acceptSelector.close();
            serverSocketChannel.close();
            assertEquals(0, SelectionKey.getMapSize());
        } catch (IOException e) {
            fail();
        }
    }

}
