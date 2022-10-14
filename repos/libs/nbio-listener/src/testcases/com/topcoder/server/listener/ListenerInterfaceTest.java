package com.topcoder.server.listener;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import junit.framework.TestCase;

import com.topcoder.netCommon.io.ClientSocket;
import com.topcoder.netCommon.io.ObjectReaderTest;
import com.topcoder.netCommon.io.RandomUtils;
import com.topcoder.server.listener.monitor.MonitorInterface;
import com.topcoder.server.listener.util.concurrent.CountDown;
import com.topcoder.shared.netCommon.SimpleCSHandler;
import com.topcoder.shared.netCommon.SimpleCSHandlerFactory;
import com.topcoder.shared.util.logging.Logger;

public final class ListenerInterfaceTest extends TestCase {

    private static final int PORT = AcceptHandlerTest.PORT + 200;
    private static final Logger cat = Logger.getLogger("LCTest");
    private static final ListenerFactory[] FACTORY = {
        new NBIOListenerFactory(),
    };

    private static int port_id = PORT;

    public ListenerInterfaceTest(String name) {
        super(name);
    }

    private static void info(String message) {
        cat.info(message);
    }

    private static ClientSocket newClientSocket(InetAddress address, int port) throws IOException {
        return new ClientSocket(address, port, new SimpleCSHandler());
    }

    private static class Client implements Runnable {

        private final int M = 100;

        private static int count;

        private final List out = new ArrayList();
        private final List in = new ArrayList();
        private final Object lock;
        private final String name;
        private final int port;
        private final Logger cat;

        private ClientSocket socket;
        private long timeDiff;
        private int outCount;

        private Client(int port, int id, Object lock, Logger cat) {
            synchronized (lock) {
                count++;
            }
            this.lock = lock;
            this.port = port;
            this.cat = cat;
            for (int i = 0; i < M; i++) {
                String s = RandomUtils.randomString();
                out.add(new ObjectReaderTest.SmallRequest(id, s, i));
            }
            name = "Client " + id;
        }

        private void debug(String message) {
            cat.debug(message);
        }

        private void start() {
            try {
                socket = newClientSocket(AcceptHandlerTest.HOST, port);
            } catch (IOException e) {
                fail();
            }
            Thread thread = new Thread(this, name);
            thread.start();
        }

        private long getTimeDiff() {
            return timeDiff;
        }

        public void run() {
            try {
                for (int i = 0; i < out.size(); i++, outCount++) {
                    ObjectReaderTest.SmallRequest request = (ObjectReaderTest.SmallRequest) out.get(i);
                    try {
                        request.setStartTime(System.currentTimeMillis());
                        socket.writeObject(request);
                    } catch (IOException e) {
                        fail();
                    }
                }
            } catch (Throwable t) {
                if (t instanceof RuntimeException) {
                    throw ((RuntimeException) t);
                }
                if (t instanceof Error) {
                    throw ((Error) t);
                }
            } finally {
                synchronized (lock) {
                    while (in.size() < out.size()) {
                        try {
                            lock.wait(3000);
                        } catch (InterruptedException e) {
                            fail();
                        }
                        debug("sizes: " + in.size() + " " + out.size());
                    }
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
            assertEquals(out, in);
        }

        private void close() {
            try {
                socket.close();
            } catch (IOException e) {
                fail();
            }
        }

        private void process(Object request) {
            in.add(request);
        }

    }

    private static abstract class BaseProcessor implements ProcessorInterface {

        final List connections = Collections.synchronizedList(new ArrayList());

        ListenerInterface controller;
        CountDown acceptCount;

        void setAcceptCount(CountDown acceptCount) {
            this.acceptCount = acceptCount;
        }

        public void setListener(ListenerInterface controller) {
            this.controller = controller;
        }

        public void setMonitor(MonitorInterface monitor) {
        }

        int getConnectionsSize() {
            return connections.size();
        }

        List getConnections() {
            return connections;
        }

        public void newConnection(int id, String remoteIP) {
            assertTrue(connections.add(new Integer(id)));
            if (acceptCount != null) {
                acceptCount.release();
            }
        }

        public void lostConnection(int id) {
            String msg = id + " " + connections;
            assertTrue(msg, connections.remove(new Integer(id)));
        }

        public void start() {
        }

        public void stop() {
        }

        public void receive(int connection_id, Object request) {
        }
        public void lostConnectionTemporarily(int id) {
            String msg = id + " " + connections;
            assertTrue(msg, connections.remove(new Integer(id)));
        }
    }

    static class TestProcessor extends BaseProcessor {

        private final Map messages = new HashMap();
        private final Client client[];
        private final Object lock;

        private TestProcessor() {
            this(null, null);
        }

        TestProcessor(Client[] client, Object lock) {
            this.client = client;
            this.lock = lock;
        }

        public String toString() {
            return "TestProcessor";
        }

        public void newConnection(int id, String remoteIP) {
            super.newConnection(id, remoteIP);
            if (lock != null) {
                synchronized (lock) {
                    lock.notifyAll();
                }
            }
        }

        private void shutdown(int id) {
            lostConnection(id);
            controller.shutdown(id);
        }

        Map getMessages() {
            return messages;
        }

        public void receive(int id, Object request) {
            // we can't make such a promise, lostConnection() could go through before process()
            //assertTrue(connections.contains(new Integer(id)));
            Integer i = new Integer(id);
            List list = (List) messages.get(i);
            if (list == null) {
                list = new ArrayList();
                messages.put(i, list);
            }
            list.add(request);
            if (client != null) {
                ObjectReaderTest.SmallRequest r = (ObjectReaderTest.SmallRequest) request;
                r.setEndTime(System.currentTimeMillis());
                int k = r.getId();
                client[k].process(r);
            }
            if (lock != null) {
                synchronized (lock) {
                    lock.notifyAll();
                }
            }
        }

    }

    private void testSimpleStartStop(ListenerFactory factory, int port) {
        ListenerInterface controller = factory.createListener(port, new TestProcessor(), new SimpleCSHandlerFactory());
        try {
            controller.start();
        } catch (IOException e) {
            fail();
        }
        controller.stop();
    }

    public void testSimpleStartStop() {
        for (int i = 0; i < FACTORY.length; i++) {
            testSimpleStartStop(FACTORY[i], port_id++);
        }
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            fail();
        }
    }

    private void testCreateClientSocket(ListenerFactory factory, int port) {
        ListenerInterface controller = factory.createListener(port, new TestProcessor(), new SimpleCSHandlerFactory());
        try {
            newClientSocket(AcceptHandlerTest.HOST, port);
            fail();
        } catch (IOException e) {
        }
        try {
            controller.start();
        } catch (IOException e) {
            fail();
        }
        try {
            ClientSocket socket = newClientSocket(AcceptHandlerTest.HOST, port);
            socket.close();
        } catch (IOException e) {
            fail("cannot create a client socket");
        }
        controller.stop();
        int time = 200;
        sleep(time);
        try {
            newClientSocket(AcceptHandlerTest.HOST, port);
            fail("createClientSocket, time: " + time);
        } catch (IOException e) {
        }
    }

    public void testCreateClientSocket() {
        for (int i = 0; i < FACTORY.length; i++) {
            testCreateClientSocket(FACTORY[i], port_id++);
        }
    }

    private void testAccept(ListenerFactory factory, int port) {
        Object lock = new Object();
        TestProcessor processor = new TestProcessor(null, lock);
        ListenerInterface controller = factory.createListener(port, processor, new SimpleCSHandlerFactory());
        try {
            controller.start();
        } catch (IOException e) {
            fail();
        }
        try {
            int n = 5;
            int expectedDelay = 166; //440;
            ClientSocket socket[] = new ClientSocket[n];
            for (int i = 0; i < n; i++) {
                socket[i] = newClientSocket(AcceptHandlerTest.HOST, port);
                long time = System.currentTimeMillis();
                synchronized (lock) {
                    while (processor.getConnectionsSize() < i + 1) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            fail();
                        }
                    }
                }
                long delay = System.currentTimeMillis() - time;
                assertTrue("accept, delay=" + delay, delay <= expectedDelay);
                assertEquals(i + 1, processor.getConnectionsSize());
            }
            for (int i = 0; i < n; i++) {
                socket[i].close();
            }
        } catch (IOException e) {
            fail();
        }
        controller.stop();
        assertEquals("did we close connections?", 0, controller.getConnectionsSize());
        assertEquals(0, processor.getConnectionsSize());
    }

    public void testAccept() {
        for (int i = 0; i < FACTORY.length; i++) {
            testAccept(FACTORY[i], port_id++);
        }
    }

    private void testCloseSocket(ListenerFactory factory, int port) {
        ListenerInterface controller = factory.createListener(port, new TestProcessor(), new SimpleCSHandlerFactory());
        try {
            controller.start();
        } catch (IOException e) {
            fail();
        }
        try {
            int n = 10;
            for (int i = 0; i < n; i++) {
                ClientSocket socket = newClientSocket(AcceptHandlerTest.HOST, port);
                socket.close();
            }
        } catch (IOException e) {
            fail("" + e);
        }
        controller.stop();
    }

    public void testCloseSocket() {
        for (int i = 0; i < FACTORY.length; i++) {
            testCloseSocket(FACTORY[i], port_id++);
        }
    }

    private void testClosingConnections(ListenerFactory factory, int port) {
        ListenerInterface controller = factory.createListener(port, new TestProcessor(), new SimpleCSHandlerFactory());
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
        controller.stop();
        try {
            for (int i = 0; i < 1; i++) {
                socket.readObject();
            }
            fail("was able to write");
        } catch (ObjectStreamException e) {
            fail();
        } catch (IOException e) {
        }
        try {
            socket.close();
        } catch (IOException e) {
            fail();
        }
    }

    public void testClosingConnections() {
        for (int i = 0; i < FACTORY.length; i++) {
            testClosingConnections(FACTORY[i], port_id++);
        }
    }

    private void testReadOnce(ListenerFactory factory, int port) {
        Object lock = new Object();
        TestProcessor processor = new TestProcessor(null, lock);
        ListenerInterface controller = factory.createListener(port, processor, new SimpleCSHandlerFactory());
        try {
            controller.start();
        } catch (IOException e) {
            fail();
        }
        ClientSocket socket;
        try {
            socket = newClientSocket(AcceptHandlerTest.HOST, port);
            String msg = "just a String message";
            socket.writeObject(msg);
            long time = System.currentTimeMillis();
            synchronized (lock) {
                for (; ;) {
                    Collection set = processor.getConnections();
                    if (set.size() > 0) {
                        Integer id = (Integer) set.iterator().next();
                        List list = (List) processor.getMessages().get(id);
                        if (list != null && list.size() > 0) {
                            break;
                        }
                    }
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        fail();
                    }
                }
            }
            long delay = System.currentTimeMillis() - time;
            long expectedDelay = 281; //440;
            Collection set = processor.getConnections();
            assertEquals(1, set.size());
            Integer id = (Integer) set.iterator().next();
            assertEquals(0, id.intValue());
            List list = (List) processor.getMessages().get(id);
            assertNotNull(list);
            assertEquals(1, list.size());
            assertEquals(msg, list.get(0));
            assertTrue("readOnce, delay=" + delay + ", expected=" + expectedDelay, delay <= expectedDelay);
            socket.close();
        } catch (IOException e) {
            fail();
        }
        controller.stop();
    }

    public void testReadOnce() {
        for (int i = 0; i < FACTORY.length; i++) {
            testReadOnce(FACTORY[i], port_id++);
        }
    }

    private void testBadClient(ListenerFactory factory, int port) {
        ListenerInterface controller = factory.createListener(port, new TestProcessor(), new SimpleCSHandlerFactory());
        try {
            controller.start();
        } catch (IOException e) {
            fail();
        }
        Socket socket = null;
        OutputStream os = null;
        try {
            socket = new Socket(AcceptHandlerTest.HOST, port);
            os = socket.getOutputStream();
        } catch (IOException e) {
            fail();
        }
        int n = 9;
        byte b[] = new byte[n];
        Random rand = new Random();
        rand.nextBytes(b);
        int delay = 70;
        try {
            for (int i = 0; i < n; i++) {
                sleep(delay);
                os.write(b[i]);
                os.flush();
            }
            fail("server allowed to write garbage");
        } catch (IOException e) {
        }
        try {
            os.close();
            socket.close();
        } catch (IOException e) {
            fail();
        }
        controller.stop();
    }

    public void testBadClient() {
        for (int i = 0; i < FACTORY.length; i++) {
            testBadClient(FACTORY[i], port_id++);
        }
    }

    private void testReadManyThreads(ListenerFactory factory, int port) {
        int n = 2;
        Client client[] = new Client[n];
        Object lock = new Object();
        for (int i = 0; i < n; i++) {
            client[i] = new Client(port, i, lock, cat);
        }
        TestProcessor processor = new TestProcessor(client, lock);
        ListenerInterface controller = factory.createListener(port, processor, new SimpleCSHandlerFactory());
        try {
            controller.start();
        } catch (IOException e) {
            fail();
        }
        for (int i = 0; i < n; i++) {
            client[i].start();
        }
        synchronized (lock) {
            while (Client.count > 0) {
                long delay = 3000;
                long time = System.currentTimeMillis();
                try {
                    lock.wait(delay);
                } catch (InterruptedException e) {
                    fail();
                }
                if (System.currentTimeMillis() - time > delay / 2) {
                    info("count: " + Client.count);
                    StringBuffer buf = new StringBuffer();
                    for (int i = 0; i < n; i++) {
                        buf.append(client[i].in.size() + " ");
                    }
                    info("in sizes: " + buf);
                    buf.setLength(0);
                    for (int i = 0; i < n; i++) {
                        buf.append(client[i].outCount + " ");
                    }
                    info("outCount: " + buf);
                }
            }
        }
        for (int i = 0; i < n; i++) {
            client[i].close();
        }
        long expectedTimeDiff = 1082; //4290;
        for (int i = 0; i < n; i++) {
            long timeDiff = client[i].getTimeDiff();
            assertTrue("readManyThreads, timeDiff=" + timeDiff, timeDiff <= expectedTimeDiff);
        }
        controller.stop();
    }

    public void testReadManyThreads() {
        for (int i = 0; i < FACTORY.length; i++) {
            testReadManyThreads(FACTORY[i], port_id++);
        }
    }

    private void testShutdown(ListenerFactory factory, int port) {
        Object lock = new Object();
        TestProcessor processor = new TestProcessor(null, lock);
        ListenerInterface controller = factory.createListener(port, processor, new SimpleCSHandlerFactory());
        processor.setListener(controller);
        ClientSocket socket = null;
        try {
            controller.start();
        } catch (IOException e) {
            fail();
        }
        try {
            socket = newClientSocket(AcceptHandlerTest.HOST, port);
        } catch (IOException e) {
            fail();
        }
        try {
            String msg = "just a String message";
            socket.writeObject(msg);
            synchronized (lock) {
                for (; ;) {
                    Collection set = processor.getConnections();
                    if (set.size() > 0) {
                        Integer id = (Integer) set.iterator().next();
                        List list = (List) processor.getMessages().get(id);
                        if (list != null && list.size() > 0) {
                            break;
                        }
                    }
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        fail();
                    }
                }
            }
            Collection set = processor.getConnections();
            assertEquals(1, set.size());
            Integer id = (Integer) set.iterator().next();
            assertEquals(0, id.intValue());
            List list = (List) processor.getMessages().get(id);
            assertNotNull(list);
            assertEquals(1, list.size());
            assertEquals(msg, list.get(0));
        } catch (IOException e) {
            fail();
        }
        processor.shutdown(0);
        try {
            socket.readObject();
            fail();
        } catch (ObjectStreamException e) {
            fail();
        } catch (IOException e) {
        }
        try {
            socket.close();
        } catch (IOException e) {
            fail();
        }
        controller.stop();
    }

    public void testShutdown() {
        for (int i = 0; i < FACTORY.length; i++) {
            testShutdown(FACTORY[i], port_id++);
        }
    }

    static void manyAccepts(ListenerFactory factory, int port, int n, long expected) {
        TestProcessor processor = new TestProcessor();
        CountDown count = new CountDown(n);
        processor.setAcceptCount(count);
        ListenerInterface controller = factory.createListener(port, processor, new SimpleCSHandlerFactory());
        try {
            controller.start();
        } catch (IOException e) {
            fail();
        }
        Socket socket[] = new Socket[n];
        long start = System.currentTimeMillis();
        try {
            for (int i = 0; i < n; i++) {
                socket[i] = new Socket(AcceptHandlerTest.HOST, port);
            }
        } catch (IOException e) {
            fail("" + e);
        }
        try {
            count.acquire();
        } catch (InterruptedException e) {
            fail();
        }
        long elapsed = System.currentTimeMillis() - start;
        try {
            for (int i = 0; i < n; i++) {
                socket[i].close();
            }
        } catch (IOException e) {
            fail();
        }
        controller.stop();
        long avg = elapsed / n;
        assertTrue("manyAccepts, avg=" + avg + ", expected=" + expected, avg <= expected);
        info("avg=" + avg);
    }

    private void testManyAccepts(ListenerFactory factory, int port) {
        int n = 63;
        long expected = 80;
        manyAccepts(factory, port, n, expected);
    }

    public void testManyAccepts() {
        for (int i = 0; i < FACTORY.length; i++) {
            testManyAccepts(FACTORY[i], port_id++);
        }
    }

    private static class CloseProcessor extends BaseProcessor {

        public void newConnection(int connection_id, String remoteIP) {
            controller.shutdown(connection_id);
            super.newConnection(connection_id, remoteIP);
        }
    }

    private void testImmediatelyClose(ListenerFactory factory, int port) {
        int n = 10;
        CloseProcessor processor = new CloseProcessor();
        CountDown count = new CountDown(n);
        processor.setAcceptCount(count);
        ListenerInterface controller = factory.createListener(port, processor, new SimpleCSHandlerFactory());
        try {
            controller.start();
        } catch (IOException e) {
            fail();
        }
        Socket socket[] = new Socket[n];
        try {
            for (int i = 0; i < n; i++) {
                socket[i] = new Socket(AcceptHandlerTest.HOST, port);
            }
        } catch (IOException e) {
            fail();
        }
        try {
            count.acquire();
        } catch (InterruptedException e) {
            fail();
        }
        try {
            for (int i = 0; i < n; i++) {
                socket[i].close();
            }
        } catch (IOException e) {
            fail();
        }
        controller.stop();
    }

    public void testImmediatelyClose() {
        for (int i = 0; i < FACTORY.length; i++) {
            testImmediatelyClose(FACTORY[i], port_id++);
        }
    }

    private static class AsyncClient implements Runnable {

        private final ClientSocket socket;
        private final CountDown count;

        private AsyncClient(InetAddress host, int port, CountDown count) throws IOException {
            this.count = count;
            socket = newClientSocket(host, port);
            (new Thread(this)).start();
            new Reader(socket);
        }

        public void run() {
            int n = 512;
            for (; ;) {
                String msg = RandomUtils.randomString(n);
                try {
                    socket.writeObject(msg);
                } catch (IOException e) {
                    break;
                }
            }
            count.release();
        }

        private void close() throws IOException {
            socket.close();
        }

        private static class Reader implements Runnable {

            private final ClientSocket socket;

            private Reader(ClientSocket socket) {
                this.socket = socket;
                (new Thread(this)).start();
            }

            public void run() {
                for (; ;) {
                    try {
                        socket.readObject();
                    } catch (IOException e) {
                        break;
                    }
                }
            }

        }

    }

    private static class AsyncCloseProcessor extends BaseProcessor implements Runnable {

        private AsyncCloseProcessor(int n) {
            setAcceptCount(new CountDown(n));
            (new Thread(this)).start();
        }

        public void receive(int connection_id, Object request) {
            controller.send(connection_id, request);
        }

        public void run() {
            try {
                acceptCount.acquire();
            } catch (InterruptedException e) {
                fail();
            }
            Random rand = new Random();
            int n;
            while ((n = connections.size()) > 0) {
                try {
                    Thread.sleep(40);
                } catch (InterruptedException e) {
                    fail();
                }
                int k = rand.nextInt(n);
                Integer id = (Integer) connections.get(k);
                controller.shutdown(id.intValue());
            }
        }

    }

    private void testAsyncClose(ListenerFactory factory, int port) {
        int n = 32;
        AsyncCloseProcessor processor = new AsyncCloseProcessor(n);
        CountDown count = new CountDown(n);
        ListenerInterface controller = factory.createListener(port, processor, new SimpleCSHandlerFactory());
        try {
            controller.start();
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
        AsyncClient client[] = new AsyncClient[n];
        int i = 0;
        try {
            for (i = 0; i < n; i++) {
                client[i] = new AsyncClient(AcceptHandlerTest.HOST, port, count);
            }
        } catch (IOException e) {
            fail("i=" + i + ", " + e);
        }
        try {
            count.acquire();
        } catch (InterruptedException e) {
            fail();
        }
        try {
            for (i = 0; i < n; i++) {
                client[i].close();
            }
        } catch (IOException e) {
            fail();
        }
        controller.stop();
        assertEquals(0, controller.getResponseQueueSize());
    }

    public void testAsyncClose() {
        for (int i = 0; i < FACTORY.length; i++) {
            testAsyncClose(FACTORY[i], port_id++);
        }
    }

}
