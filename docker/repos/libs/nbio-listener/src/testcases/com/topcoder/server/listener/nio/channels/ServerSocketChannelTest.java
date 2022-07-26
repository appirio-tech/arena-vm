package com.topcoder.server.listener.nio.channels;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Set;

import junit.framework.TestCase;

import com.topcoder.server.listener.AcceptHandlerTest;
import com.topcoder.server.listener.net.InetSocketAddress;

public final class ServerSocketChannelTest extends TestCase {

    private static final int PORT = AcceptHandlerTest.PORT + 300;
    private static final int BACKLOG = 511;

    public ServerSocketChannelTest(String name) {
        super(name);
    }

    public void testRead() {
        int port = PORT;
        ServerSocketChannel serverSocketChannel = null;
        Selector acceptSelector = null;
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            InetSocketAddress address = new InetSocketAddress(AcceptHandlerTest.HOST, port);
            serverSocketChannel.socket().bind(address, BACKLOG);
            acceptSelector = Selector.open();
            serverSocketChannel.register(acceptSelector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            fail();
        }
        Socket socket = null;
        try {
            socket = new Socket(AcceptHandlerTest.HOST, port);
            assertEquals(1, acceptSelector.select());
            Set set = acceptSelector.selectedKeys();
            assertEquals(1, set.size());
            for (Iterator it = set.iterator(); it.hasNext();) {
                SelectionKey key = (SelectionKey) it.next();
                assertEquals(null, key.attachment());
                it.remove();
            }
        } catch (IOException e) {
            fail();
        }
        SocketChannel socketChannel = null;
        Selector readSelector = null;
        try {
            socketChannel = serverSocketChannel.accept();
            //assertEquals(0,acceptSelector.selectNow());
            socketChannel.configureBlocking(false);
            readSelector = Selector.open();
            socketChannel.register(readSelector, SelectionKey.OP_READ);
            assertEquals(0, readSelector.selectNow());
        } catch (IOException e) {
            fail();
        }
        int m = 17;
        try {
            OutputStream os = socket.getOutputStream();
            os.write(m);
            os.flush();
            assertEquals(1, readSelector.select());
            Set set = readSelector.selectedKeys();
            assertEquals(1, set.size());
            for (Iterator it = set.iterator(); it.hasNext();) {
                SelectionKey key = (SelectionKey) it.next();
                assertEquals(null, key.attachment());
                it.remove();
            }
        } catch (IOException e) {
            fail();
        }
        try {
            java.nio.ByteBuffer buffer = ByteBuffer.allocate(10);
            int br = socketChannel.read(buffer);
            assertEquals(0, readSelector.selectNow());
            assertEquals(1, br);
            assertEquals("we didn't put a byte in a buffer?", 1, buffer.position());
            buffer.flip();
            assertEquals(m, buffer.get());
        } catch (IOException e) {
            fail();
        }
        try {
            readSelector.close();
            socketChannel.close();
            socket.close();
            acceptSelector.close();
            serverSocketChannel.close();
        } catch (IOException e) {
            fail();
        }
    }

    /*
    public void testWrite() {
        int port=PORT+1;
        ServerSocketChannel serverSocketChannel=null;
        Selector acceptSelector=null;
        try {
            serverSocketChannel=ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            InetSocketAddress address=new InetSocketAddress(AcceptHandlerTest.HOST,port);
            serverSocketChannel.socket().bind(address,BACKLOG);
            acceptSelector=Selector.open();
            serverSocketChannel.register(acceptSelector,SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            fail();
        }
        Socket socket=null;
        try {
            socket=new Socket(AcceptHandlerTest.HOST,port);
            assertEquals(1,acceptSelector.select());
            Set set=acceptSelector.selectedKeys();
            assertEquals(1,set.size());
            for (Iterator it=set.iterator(); it.hasNext(); ) {
                SelectionKey key=(SelectionKey) it.next();
                assertEquals(null,key.attachment());
                it.remove();
            }
        } catch (IOException e) {
            fail();
        }
        SocketChannel socketChannel=null;
        Selector writeSelector=null;
        try {
            socketChannel=serverSocketChannel.accept();
            assertEquals(0,acceptSelector.selectNow());
            socketChannel.configureBlocking(false);
            writeSelector=Selector.open();
            socketChannel.register(writeSelector,SelectionKey.OP_WRITE);
            assertEquals(1,writeSelector.select());
        } catch (IOException e) {
            fail();
        }
        int m=17;
        try {
            Set set=writeSelector.selectedKeys();
            assertEquals(1,set.size());
            for (Iterator it=set.iterator(); it.hasNext(); ) {
                SelectionKey key=(SelectionKey) it.next();
                assertEquals(null,key.attachment());
                it.remove();
            }
            ByteBuffer buffer=ByteBuffer.allocate(10);
            buffer.put((byte)m);
            buffer.flip();
            int bw=socketChannel.write(buffer);
            assertEquals(1,bw);
            assertEquals(1,buffer.position());
        } catch (IOException e) {
            fail();
        }
        try {
            InputStream is=socket.getInputStream();
            int b=is.read();
            assertEquals(m,b);
        } catch (IOException e) {
            fail();
        }
        try {
            writeSelector.close();
            socketChannel.close();
            socket.close();
            acceptSelector.close();
            serverSocketChannel.close();
        } catch (IOException e) {
            fail();
        }
    }

    public void testWriteRead() {
        int port=PORT+2;
        ServerSocketChannel serverSocketChannel=null;
        Selector acceptSelector=null;
        try {
            serverSocketChannel=ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            InetSocketAddress address=new InetSocketAddress(AcceptHandlerTest.HOST,port);
            serverSocketChannel.socket().bind(address,BACKLOG);
            acceptSelector=Selector.open();
            serverSocketChannel.register(acceptSelector,SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            fail();
        }
        Socket socket=null;
        try {
            socket=new Socket(AcceptHandlerTest.HOST,port);
            assertEquals(1,acceptSelector.select());
            Set set=acceptSelector.selectedKeys();
            assertEquals(1,set.size());
            for (Iterator it=set.iterator(); it.hasNext(); ) {
                SelectionKey key=(SelectionKey) it.next();
                assertEquals(null,key.attachment());
                it.remove();
            }
        } catch (IOException e) {
            fail();
        }
        SocketChannel socketChannel=null;
        try {
            socketChannel=serverSocketChannel.accept();
            assertEquals(0,acceptSelector.selectNow());
            socketChannel.configureBlocking(false);
        } catch (IOException e) {
            fail();
        }
        Selector writeSelector=null;
        try {
            writeSelector=Selector.open();
            socketChannel.register(writeSelector,SelectionKey.OP_WRITE);
            assertEquals(1,writeSelector.select());
        } catch (IOException e) {
            fail();
        }
        Selector readSelector=null;
        try {
            readSelector=Selector.open();
            socketChannel.register(readSelector,SelectionKey.OP_READ);
            assertEquals(0,readSelector.selectNow());
        } catch (IOException e) {
            fail();
        }
        int m=17;
        try {
            Set set=writeSelector.selectedKeys();
            assertEquals(1,set.size());
            for (Iterator it=set.iterator(); it.hasNext(); ) {
                SelectionKey key=(SelectionKey) it.next();
                assertEquals(null,key.attachment());
                it.remove();
            }
            ByteBuffer buffer=ByteBuffer.allocate(10);
            buffer.put((byte)m);
            buffer.flip();
            int bw=socketChannel.write(buffer);
            assertEquals(1,bw);
            assertEquals(1,buffer.position());
        } catch (IOException e) {
            fail();
        }
        try {
            InputStream is=socket.getInputStream();
            int b=is.read();
	    assertEquals(m,b);
        } catch (IOException e) {
            fail();
        }
        m=19;
        try {
            OutputStream os=socket.getOutputStream();
            os.write(m);
            os.flush();
            assertEquals(1,readSelector.select());
            Set set=readSelector.selectedKeys();
            assertEquals(1,set.size());
            for (Iterator it=set.iterator(); it.hasNext(); ) {
                SelectionKey key=(SelectionKey) it.next();
                assertEquals(null,key.attachment());
                it.remove();
            }
        } catch (IOException e) {
            fail();
        }
        try {
            ByteBuffer buffer=ByteBuffer.allocate(10);
            int br=socketChannel.read(buffer);
            assertEquals(0,readSelector.selectNow());
            assertEquals(1,br);
            assertEquals("we didn't put a byte in a buffer?",1,buffer.position());
            buffer.flip();
            assertEquals(m,buffer.get());
        } catch (IOException e) {
            fail();
        }
        try {
            readSelector.close();
            writeSelector.close();
            socketChannel.close();
            socket.close();
            acceptSelector.close();
            serverSocketChannel.close();
        } catch (IOException e) {
            fail();
        }
    }
    */

}
