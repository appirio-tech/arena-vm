package com.topcoder.server.listener;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import com.topcoder.netCommon.io.ClientSocket;
import com.topcoder.netCommon.io.IOConstants;
import com.topcoder.netCommon.io.ObjectReaderTest;
import com.topcoder.netCommon.io.RandomUtils;
import com.topcoder.server.listener.monitor.EmptyMonitor;
import com.topcoder.shared.netCommon.SimpleCSHandler;
import com.topcoder.shared.netCommon.SimpleCSHandlerFactory;

public final class ListenerInterfaceTest2 extends TestCase {

    private static final int PORT = AcceptHandlerTest.PORT + 500;
    private static final ListenerFactory[] FACTORY = {
        new NBIOListenerFactory(),
    };

    private static int port_id = PORT;

    public ListenerInterfaceTest2(String name) {
        super(name);
    }

    private static class ReadThread implements Runnable {

        private static int count;

        private final Object lock;
        private final ClientSocket socket;
        private final List in = new ArrayList();
        private final int limit;

        private long timeDiff;

        private ReadThread(ClientSocket socket, Object lock, int limit) {
            synchronized (lock) {
                count++;
            }
            this.lock = lock;
            this.socket = socket;
            this.limit = limit;
            (new Thread(this)).start();
        }

        public void run() {
            for (int i = 0; i < limit; i++) {
                Object object = null;
                try {
                    object = socket.readObject();
                } catch (ObjectStreamException e) {
                    fail(i + " " + e);
                } catch (IOException e) {
                    fail();
                }
                ObjectReaderTest.SmallRequest r = (ObjectReaderTest.SmallRequest) object;
                r.setEndTime(System.currentTimeMillis());
                in.add(object);
            }
            try {
                socket.close();
            } catch (IOException e) {
                fail();
            }
            long max = -1;
            for (Iterator it = in.iterator(); it.hasNext();) {
                ObjectReaderTest.SmallRequest r = (ObjectReaderTest.SmallRequest) it.next();
                max = Math.max(max, r.getTimeDiff());
            }
            timeDiff = max;
            synchronized (lock) {
                count--;
                if (count == 0) {
                    lock.notifyAll();
                }
            }
        }

    }

    private static class WriteClient implements Runnable {

        private static final int M = 100;

        private final String name;
        private final List out = new ArrayList();
        private final int id;
        private final ListenerInterface controller;

        private WriteClient(int id, ListenerInterface controller) {
            this.id = id;
            this.controller = controller;
            for (int i = 0; i < M; i++) {
                String s = RandomUtils.randomString();
                out.add(new ObjectReaderTest.SmallRequest(id, s, i));
            }
            name = "WriteClient " + id;
        }

        private void start() {
            Thread thread = new Thread(this, name);
            thread.start();
        }

        public void run() {
            for (int i = 0; i < out.size(); i++) {
                ObjectReaderTest.SmallRequest request = (ObjectReaderTest.SmallRequest) out.get(i);
                request.setStartTime(System.currentTimeMillis());
                controller.send(id, request);
            }
        }

    }

    private static ClientSocket newClientSocket(InetAddress address, int port) throws IOException {
        return new ClientSocket(address, port, new SimpleCSHandler());
    }

    private void testWriteOnce(ListenerFactory factory, int port) {
        Object lock = new Object();
        ListenerInterfaceTest.TestProcessor processor = new ListenerInterfaceTest.TestProcessor(null, lock);
        ListenerInterface controller = factory.createListener(port, processor, new SimpleCSHandlerFactory());
        try {
            controller.start();
        } catch (IOException e) {
            fail();
        }
        try {
            ClientSocket socket = newClientSocket(AcceptHandlerTest.HOST, port);
            String msg = RandomUtils.randomString();
            Integer id;
            synchronized (lock) {
                for (; ;) {
                    Collection set = processor.getConnections();
                    if (set.size() > 0) {
                        id = (Integer) set.iterator().next();
                        break;
                    }
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        fail();
                    }
                }
            }
            controller.send(id.intValue(), msg);
            assertEquals(msg, socket.readObject());
            socket.close();
        } catch (IOException e) {
            fail();
        }
        controller.stop();
    }

    public void testWriteOnce() {
        for (int i = 0; i < FACTORY.length; i++) {
            testWriteOnce(FACTORY[i], port_id++);
        }
    }

    private void testWriteMany(ListenerFactory factory, int port) {
        int n = 3;
        WriteClient writeClient[] = new WriteClient[n];
        Object lock = new Object();
        ListenerInterfaceTest.TestProcessor processor = new ListenerInterfaceTest.TestProcessor(null, lock);
        ListenerInterface controller = factory.createListener(port, processor, new SimpleCSHandlerFactory());
        for (int i = 0; i < n; i++) {
            writeClient[i] = new WriteClient(i, controller);
        }
        try {
            controller.start();
        } catch (IOException e) {
            fail();
        }
        ReadThread readClient[] = new ReadThread[n];
        ReadThread.count = 0;
        try {
            for (int i = 0; i < n; i++) {
                ClientSocket socket = newClientSocket(AcceptHandlerTest.HOST, port);
                readClient[i] = new ReadThread(socket, lock, WriteClient.M);
            }
        } catch (IOException e) {
            fail();
        }
        synchronized (lock) {
            for (; ;) {
                Collection set = processor.getConnections();
                if (set.size() >= n) {
                    assertEquals(n, set.size());
                    Collection set2 = new ArrayList();
                    for (int i = 0; i < n; i++) {
                        set2.add(new Integer(i));
                    }
                    assertEquals(set, set2);
                    break;
                }
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    fail();
                }
            }
        }
        for (int i = 0; i < n; i++) {
            writeClient[i].start();
        }
        synchronized (lock) {
            while (ReadThread.count > 0) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    fail();
                }
            }
        }
        for (int i = 0; i < n; i++) {
            assertEquals(writeClient[i].out.size(), readClient[i].in.size());
            assertEquals(writeClient[i].out, readClient[i].in);
        }
        long expectedTimeDiff = 19048;
        for (int i = 0; i < n; i++) {
            long timeDiff = readClient[i].timeDiff;
            assertTrue("writeMany, timeDiff=" + timeDiff, timeDiff <= expectedTimeDiff);
        }
        controller.stop();
    }

    public void testWriteMany() {
        for (int i = 0; i < FACTORY.length; i++) {
            testWriteMany(FACTORY[i], port_id++);
        }
    }

    private void testEchoOnce(ListenerFactory factory, int port) {
        Object lock = new Object();
        EchoProcessor processor = new EchoProcessor();
        ListenerInterface controller = factory.createListener(port, processor, new SimpleCSHandlerFactory());
        try {
            controller.start();
        } catch (IOException e) {
            fail();
        }
        ClientSocket socket = null;
        try {
            socket = newClientSocket(AcceptHandlerTest.HOST, port);
        } catch (IOException e) {
            fail();
        }
        ObjectReaderTest.SmallRequest msg = new ObjectReaderTest.SmallRequest(0, "just a message");
        ReadThread.count = 0;
        ReadThread readThread = null;
        try {
            readThread = new ReadThread(socket, lock, 1);
            msg.setStartTime(System.currentTimeMillis());
            socket.writeObject(msg);
        } catch (IOException e) {
            fail();
        }
        synchronized (lock) {
            while (ReadThread.count > 0) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    fail();
                }
            }
        }
        assertEquals(1, readThread.in.size());
        assertEquals(msg, readThread.in.get(0));
        long expectedTimeDiff = 240;
        long timeDiff = readThread.timeDiff;
        assertTrue("echoOnce, timeDiff=" + timeDiff, timeDiff <= expectedTimeDiff);
        controller.stop();
    }

    public void testEchoOnce() {
        for (int i = 0; i < FACTORY.length; i++) {
            testEchoOnce(FACTORY[i], port_id++);
        }
    }

    private void testEchoMany(ListenerFactory factory, int port) {
        Object lock = new Object();
        EchoProcessor processor = new EchoProcessor();
        ListenerInterface controller = factory.createListener(port, processor, new SimpleCSHandlerFactory());
        try {
            controller.start();
        } catch (IOException e) {
            fail();
        }
        ClientSocket socket = null;
        try {
            socket = newClientSocket(AcceptHandlerTest.HOST, port);
        } catch (IOException e) {
            fail();
        }
        int n = 100;
        List out = new ArrayList();
        for (int i = 0; i < n; i++) {
            String s = RandomUtils.randomString();
            out.add(new ObjectReaderTest.SmallRequest(0, s, i));
        }
        ReadThread.count = 0;
        ReadThread readThread = null;
        try {
            readThread = new ReadThread(socket, lock, n);
            for (int i = 0; i < n; i++) {
                ObjectReaderTest.SmallRequest msg = (ObjectReaderTest.SmallRequest) out.get(i);
                msg.setStartTime(System.currentTimeMillis());
                socket.writeObject(msg);
            }
        } catch (IOException e) {
            fail();
        }
        synchronized (lock) {
            while (ReadThread.count > 0) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    fail();
                }
            }
        }
        assertEquals(n, readThread.in.size());
        assertEquals(out, readThread.in);
        long expectedTimeDiff = 10805;
        long timeDiff = readThread.timeDiff;
        assertTrue("echoMany, timeDiff=" + timeDiff, timeDiff <= expectedTimeDiff);
        controller.stop();
    }

    public void testEchoMany() {
        for (int i = 0; i < FACTORY.length; i++) {
            testEchoMany(FACTORY[i], port_id++);
        }
    }

    private void testEchoManyThreads(ListenerFactory factory, int port) {
        int n = 3;
        Object lock = new Object();
        EchoProcessor processor = new EchoProcessor();
        ListenerInterface controller = factory.createListener(port, processor, new SimpleCSHandlerFactory());
        try {
            controller.start();
        } catch (IOException e) {
            fail();
        }
        EchoClient echoClient[] = new EchoClient[n];
        ReadThread.count = 0;
        try {
            for (int i = 0; i < n; i++) {
                echoClient[i] = new EchoClient(AcceptHandlerTest.HOST, port, lock, 100);
            }
        } catch (IOException e) {
            fail();
        }
        synchronized (lock) {
            while (ReadThread.count > 0) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    fail();
                }
            }
        }
        for (int i = 0; i < n; i++) {
            assertEquals(echoClient[i].writeThread.out.size(), echoClient[i].readThread.in.size());
            assertEquals(echoClient[i].writeThread.out, echoClient[i].readThread.in);
        }
        long expectedTimeDiff = 16544;
        for (int i = 0; i < n; i++) {
            long timeDiff = echoClient[i].readThread.timeDiff;
            assertTrue("echoManyThreads, timeDiff=" + timeDiff, timeDiff <= expectedTimeDiff);
        }
        controller.stop();
    }

    public void testEchoManyThreads() {
        for (int i = 0; i < FACTORY.length; i++) {
            testEchoManyThreads(FACTORY[i], port_id++);
        }
    }

    private static class EchoClient {

        private final ClientSocket socket;
        private final WriteThread writeThread;
        private final ReadThread readThread;

        private EchoClient(InetAddress address, int port, Object lock, int limit) throws IOException {
            socket = newClientSocket(address, port);
            readThread = new ReadThread(socket, lock, limit);
            writeThread = new WriteThread(socket, limit);
        }

        private static class WriteThread implements Runnable {

            private final ClientSocket socket;
            private final List out = new ArrayList();

            private WriteThread(ClientSocket socket, int limit) {
                this.socket = socket;
                for (int i = 0; i < limit; i++) {
                    String s = RandomUtils.randomString();
                    out.add(new ObjectReaderTest.SmallRequest(0, s, i));
                }
                (new Thread(this)).start();
            }

            public void run() {
                for (int i = 0; i < out.size(); i++) {
                    ObjectReaderTest.SmallRequest request = (ObjectReaderTest.SmallRequest) out.get(i);
                    request.setStartTime(System.currentTimeMillis());
                    try {
                        socket.writeObject(request);
                    } catch (IOException e) {
                        fail();
                    }
                }
            }

        }

    }

    private void test_HalfBad_HalfWrite(ListenerFactory factory, int port) {
        int n = 5;
        WriteClient writeClient[] = new WriteClient[n];
        Object lock = new Object();
        ListenerInterfaceTest.TestProcessor processor = new ListenerInterfaceTest.TestProcessor(null, lock);
        ListenerInterface controller = factory.createListener(port, processor, new SimpleCSHandlerFactory());
        for (int i = 0; i < n; i++) {
            writeClient[i] = new WriteClient(i, controller);
        }
        try {
            controller.start();
        } catch (IOException e) {
            fail();
        }
        ReadThread readClient[] = new ReadThread[n];
        ReadThread.count = 0;
        try {
            for (int i = 0; i < n; i++) {
                ClientSocket socket = newClientSocket(AcceptHandlerTest.HOST, port);
                readClient[i] = new ReadThread(socket, lock, WriteClient.M);
            }
        } catch (IOException e) {
            fail();
        }
        synchronized (lock) {
            for (; ;) {
                Collection set = processor.getConnections();
                if (set.size() >= n) {
                    assertEquals(n, set.size());
                    Collection set2 = new ArrayList();
                    for (int i = 0; i < n; i++) {
                        set2.add(new Integer(i));
                    }
                    assertEquals(set, set2);
                    break;
                }
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    fail();
                }
            }
        }
        for (int i = 0; i < n; i++) {
            writeClient[i].start();
            try {
                Socket socket = new Socket(AcceptHandlerTest.HOST, port);
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject("0123456789");
                oos.flush();
                oos.close();
                socket.close();
            } catch (IOException e) {
                fail();
            }
        }
        synchronized (lock) {
            while (ReadThread.count > 0) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    fail();
                }
            }
        }
        for (int i = 0; i < n; i++) {
            assertEquals(writeClient[i].out.size(), readClient[i].in.size());
            assertEquals(writeClient[i].out, readClient[i].in);
        }
        controller.stop();
    }

    public void test_HalfBad_HalfWrite() {
        for (int i = 0; i < FACTORY.length; i++) {
            test_HalfBad_HalfWrite(FACTORY[i], port_id++);
        }
    }

    private void testWriteLongString(ListenerFactory factory, int port) {
        Object lock = new Object();
        ListenerInterfaceTest.TestProcessor processor = new ListenerInterfaceTest.TestProcessor(null, lock);
        ListenerInterface controller = factory.createListener(port, processor, new SimpleCSHandlerFactory());
        try {
            controller.start();
        } catch (IOException e) {
            fail();
        }
        try {
            ClientSocket socket = newClientSocket(AcceptHandlerTest.HOST, port);
            String msg = RandomUtils.randomString();
            Integer id;
            synchronized (lock) {
                for (; ;) {
                    Collection set = processor.getConnections();
                    if (set.size() > 0) {
                        id = (Integer) set.iterator().next();
                        break;
                    }
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        fail();
                    }
                }
            }
            controller.send(id.intValue(), msg);
            assertEquals(msg, socket.readObject());
            int bigSize = Math.max(Short.MAX_VALUE + 1, IOConstants.RESPONSE_MAXIMUM_BUFFER_SIZE + 1);
            StringBuffer bigMsg = new StringBuffer(bigSize);
            for (int i = 0; i < bigSize; i++) {
                bigMsg.append('a');
            }
            controller.send(id.intValue(), bigMsg.toString());
            String tinyMsg = "tinyMsg";
            controller.send(id.intValue(), tinyMsg);
            try {
                assertEquals(tinyMsg, socket.readObject());
            } catch (IOException e) {
                fail("" + e);
            }
            socket.close();
        } catch (IOException e) {
            fail();
        }
        controller.stop();
    }

    public void testWriteLongString() {
        for (int i = 0; i < FACTORY.length; i++) {
            testWriteLongString(FACTORY[i], port_id++);
        }
    }

    private void testNoKey(ListenerFactory factory, int port) {
        Object lock = new Object();
        ListenerInterfaceTest.TestProcessor processor = new ListenerInterfaceTest.TestProcessor(null, lock);
        ListenerInterface controller = factory.createListener(port, processor, new EmptyMonitor(), 0, 0, 1, new SimpleCSHandlerFactory());
        try {
            controller.start();
        } catch (IOException e) {
            fail();
        }
        try {
            ClientSocket socket = newClientSocket(AcceptHandlerTest.HOST, port);
            Integer id;
            synchronized (lock) {
                for (; ;) {
                    Collection set = processor.getConnections();
                    if (set.size() > 0) {
                        id = (Integer) set.iterator().next();
                        break;
                    }
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        fail();
                    }
                }
            }
            for (int i = 0; i < 2; i++) {
                String msg = RandomUtils.randomString();
                controller.send(id.intValue(), msg);
            }
            try {
                Thread.sleep(ResponseWriter.NO_KEY_SPIN_LIMIT + 1000);
            } catch (InterruptedException e) {
                fail();
            }
            assertEquals(0, processor.getConnectionsSize());
            socket.close();
        } catch (IOException e) {
            fail();
        }
        controller.stop();
    }

    public void testNoKey() {
        for (int i = 0; i < FACTORY.length; i++) {
            testNoKey(FACTORY[i], port_id++);
        }
    }

}
