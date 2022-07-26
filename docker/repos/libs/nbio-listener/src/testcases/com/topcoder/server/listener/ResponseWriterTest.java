package com.topcoder.server.listener;

import java.io.IOException;

import junit.framework.TestCase;

import com.topcoder.netCommon.io.RandomUtils;
import com.topcoder.server.listener.net.InetSocketAddress;
import com.topcoder.server.listener.nio.channels.SelectionKey;
import com.topcoder.server.listener.nio.channels.Selector;
import com.topcoder.server.listener.nio.channels.ServerSocketChannel;
import com.topcoder.server.listener.nio.channels.SocketChannel;
import com.topcoder.shared.netCommon.SimpleCSHandler;

public class ResponseWriterTest extends TestCase {

    private static final int PORT = AcceptHandlerTest.PORT + 800;

    public ResponseWriterTest(String name) {
        super(name);
    }

    private void testWriteObject(Object msg, int port) {
        ServerSocketChannel serverSocketChannel = null;
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            InetSocketAddress address = new InetSocketAddress(AcceptHandlerTest.HOST, port);
            serverSocketChannel.socket().bind(address, 511);
            Selector selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            fail();
        }
        SocketChannel readChannel = null;
        SocketChannel writeChannel = null;
        try {
            readChannel = SocketChannel.open(new InetSocketAddress(AcceptHandlerTest.HOST, port));
            writeChannel = serverSocketChannel.accept();
            writeChannel.configureBlocking(false);
        } catch (IOException e) {
            fail();
        }
        ResponseWriter writer = new ResponseWriter(new Integer(0), writeChannel, new SimpleCSHandler());
        RequestReader reader = new RequestReader(new Integer(0), readChannel, new SimpleCSHandler());
        int n = 10;
        for (int i = 0; i < n; i++) {
            assertTrue(writer.isQueueEmpty());
            writer.enqueue(msg);
            try {
                while (!writer.isQueueEmpty()) {
                    writer.write();
                }
            } catch (IOException e) {
                fail();
            }
            try {
                Object msg2;
                for (; ;) {
                    reader.read();
                    msg2 = reader.readObject();
                    if (msg2 != null) {
                        break;
                    }
                }
                assertNotNull(msg2);
                assertEquals(msg, msg2);
            } catch (IOException e) {
                fail("" + e);
            }
        }
        try {
            readChannel.close();
        } catch (IOException e) {
            fail();
        }
    }

    public void testWriteString() {
        int port = PORT + 1;
        String msg = RandomUtils.randomString();
        testWriteObject(msg, port);
    }

}
