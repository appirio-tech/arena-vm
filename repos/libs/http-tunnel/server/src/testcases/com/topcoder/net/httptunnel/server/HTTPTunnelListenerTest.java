/*
 * HTTPTunnelListenerTest
 *
 * Created 04/06/2007
 */
package com.topcoder.net.httptunnel.server;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import junit.framework.TestCase;

import com.topcoder.net.httptunnel.client.HTTPTunnelClientConnector;
import com.topcoder.netCommon.io.ClientSocket;
import com.topcoder.server.listener.ListenerInterface;
import com.topcoder.server.listener.ProcessorInterface;
import com.topcoder.server.listener.monitor.MonitorInterface;
import com.topcoder.shared.netCommon.CSHandler;
import com.topcoder.shared.netCommon.CSHandlerFactory;
import com.topcoder.shared.netCommon.SimpleCSHandler;

/**
 * Basic unit test for HTTPTunnelListener
 *
 *
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class HTTPTunnelListenerTest extends TestCase {
    private List processorEvents;
    private HTTPTunnelListener listener;
    private ProcessorInterface processor;
    private List monitorEvents;

    protected void setUp() throws Exception {
        processorEvents = Collections.synchronizedList(new ArrayList());
        monitorEvents = Collections.synchronizedList(new ArrayList());
        processor = buildProcessor();
        MonitorInterface monitor = buildMonitor();
        listener = new HTTPTunnelListener(null, 8080, processor, 2, 2, 2, monitor,
                new CSHandlerFactory() {
                    public CSHandler newInstance() {
                        return new SimpleCSHandler();
                    }

                }, new HashSet(), false, 0, Integer.MAX_VALUE);
    }


    protected void tearDown() throws Exception {
        Thread.sleep(1000);
        listener.stop();
        System.out.println("ENDING!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        dump();
    }

    public void testStart() throws Exception {
        startListener();
        Thread.sleep(500);
        listener.stop();
        checkProcessorEvents("start,stop");
    }

    public void testSendAndReceiveReuse() throws Exception {
        startListener();
        ClientSocket socket = newReusableConnection();
        socket.writeObject("Hello");
        Object result = socket.readObject();
        assertEquals("HelloHello", result);
        socket.writeObject("Bye");
        result = socket.readObject();
        assertEquals("ByeBye", result);
        Thread.sleep(200);
        checkProcessorEvents("start,new,receive,receive");
        checkMonitorEvents("new,new,assoc", true);
    }

    public void testSendReceiveCloseReuse() throws Exception {
        startListener();
        ClientSocket socket = newReusableConnection();
        socket.writeObject("Hello");
        Object result = socket.readObject();
        assertEquals("HelloHello", result);
        socket.writeObject("Bye");
        result = socket.readObject();
        assertEquals("ByeBye", result);
        socket.close();
        Thread.sleep(200);
        checkProcessorEvents("start,new,receive,receive,lost");
        checkMonitorEvents("new,new,assoc,lost,lost", true);
    }

    public void testMultiSendAndMultiReceiveReuse() throws Exception {
        startListener();
        ClientSocket socket = newReusableConnection();
        socket.writeObject("Hello");
        socket.writeObject("Bye");
        socket.writeObject("Again");
        Object result1 = socket.readObject();
        Object result2 = socket.readObject();
        Object result3 = socket.readObject();
        assertEquals("HelloHello", result1);
        assertEquals("ByeBye", result2);
        assertEquals("AgainAgain", result3);
        Thread.sleep(200);
        checkProcessorEvents("start,new,receive,receive,receive");
        checkMonitorEvents("new,new,assoc", true);
    }

    public void testSendAndReceive() throws Exception {
        startListener();
        ClientSocket socket = newCachedConnection();
        socket.writeObject("Hello");
        Object result = socket.readObject();
        assertEquals("HelloHello", result);
        socket.writeObject("Bye");
        result = socket.readObject();
        assertEquals("ByeBye", result);
        Thread.sleep(200);
        checkProcessorEvents("start,new,receive,receive");
        checkMonitorEvents("new,new,assoc,assoc", true);
    }

    public void testSendReceiveClose() throws Exception {
        startListener();
        ClientSocket socket = newCachedConnection();
        socket.writeObject("Hello");
        Object result = socket.readObject();
        assertEquals("HelloHello", result);
        socket.writeObject("Bye");
        result = socket.readObject();
        assertEquals("ByeBye", result);
        socket.close();
        Thread.sleep(200);
        checkProcessorEvents("start,new,receive,receive,lost");
        checkMonitorEvents("new,new,assoc,assoc,lost,lost", true);
    }

    public void testMultiSendAndMultiReceive() throws Exception {
        startListener();
        ClientSocket socket = newCachedConnection();
        socket.writeObject("Hello");
        socket.writeObject("Bye");
        socket.writeObject("Again");
        Object result1 = socket.readObject();
        Object result2 = socket.readObject();
        Object result3 = socket.readObject();
        assertEquals("HelloHello", result1);
        assertEquals("ByeBye", result2);
        assertEquals("AgainAgain", result3);
        Thread.sleep(5100); //Force to drop cached connection
        socket.writeObject("Again");
        result3 = socket.readObject();
        checkProcessorEvents("start,new,receive,receive,receive,receive");
        checkMonitorEvents("new,new,assoc,assoc,assoc,lost,new,assoc", true);
    }


    public void testOutputConnectionReusedByMultipleTunnels() throws Exception {
        startListener();
        ClientSocket socket1 = newCachedConnection();
        ClientSocket socket2 = newCachedConnection();
        ClientSocket socket3 = newCachedConnection();
        ClientThread thread1 = buildThread(socket1);
        ClientThread thread2 = buildThread(socket2);
        ClientThread thread3 = buildThread(socket3);
        thread1.join();
        thread2.join();
        thread3.join();
        assertFalse(thread1.isFailed());
        assertFalse(thread2.isFailed());
        assertFalse(thread3.isFailed());
    }

    private ClientThread buildThread(final ClientSocket socket) {
        return new ClientThread(socket);
    }

    private void dump() {
        System.out.println("PROCESSOR:");
        System.out.println(processorEvents);
        System.out.println("\nMONITOR");
        System.out.println(monitorEvents);
    }

    private void checkProcessorEvents(String events) {
        String[] evs = events.split(",");
        int i = 0;
        assertEquals(evs.length, processorEvents.size());
        for (Iterator it = processorEvents.iterator(); it.hasNext(); i++) {
            List l = (List) it.next();
            assertEquals(evs[i], l.get(1));
        }
    }

    private void checkMonitorEvents(String events, boolean reads) {
        String[] evs = events.split(",");
        int i = 0;
        for (Iterator it = monitorEvents.iterator(); it.hasNext();) {
            List l = (List) it.next();
            if (l.get(1).equals("read")) {
                assertTrue(reads);
            } else {
                assertEquals(evs[i], l.get(1));
                i++;
            }
        }
        assertEquals(evs.length, i);
    }

    private ClientSocket newCachedConnection() throws IOException {
        return new ClientSocket(new HTTPTunnelClientConnector("http://"+InetAddress.getLocalHost().getHostAddress()+":8080/tunnel?ignoreme=1", false), new SimpleCSHandler());
    }

    private ClientSocket newReusableConnection() throws IOException {
        return new ClientSocket(new HTTPTunnelClientConnector("http://"+InetAddress.getLocalHost().getHostAddress()+":8080/tunnel?ignoreme=1", true), new SimpleCSHandler());
    }

    protected List event(Integer id, String type, Object object) {
        return Arrays.asList(new Object[] {id, type, object});
    }

    protected List event(Integer id, String type) {
        return Arrays.asList(new Object[] {id, type, null});
    }

    private void startListener() throws Exception {
        System.out.println("STARTING!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        listener.start();
        Thread.sleep(1000);
    }

    private MonitorInterface buildMonitor() {
        return new MonitorInterface() {

            public void associateConnections(int existentConnectionId, int newConnectionID) {
                monitorEvents.add(event(new Integer(existentConnectionId), "assoc", new Integer(newConnectionID)));
                
            }

            public void newConnection(int id, String remoteIP) {
                monitorEvents.add(event(new Integer(id), "new", remoteIP));
            }

            public void lostConnection(int id) {
                monitorEvents.add(event(new Integer(id), "lost"));

            }

            public void bytesRead(int id, int numBytes) {
                monitorEvents.add(event(new Integer(id), "read", new Integer(numBytes)));

            }

        };
    }

    private ProcessorInterface buildProcessor() {
        return new ProcessorInterface() {

                    public void stop() {
                        processorEvents.add(event(null, "stop"));

                    }

                    public void start() {
                        processorEvents.add(event(null, "start"));
                    }

                    public void setListener(ListenerInterface listener) {
                        processorEvents.add(event(null, "listener", listener));
                    }

                    public void receive(int connection_id, Object request) {
                        processorEvents.add(event(new Integer(connection_id), "receive", request));
                        if (request instanceof String) {
                            if ("closeMe".equals(request)) {
                                listener.shutdown(connection_id);
                            } else {
                                listener.send(connection_id, request.toString()+ request.toString());
                            }
                        }
                    }

                    public void newConnection(int connection_id, String remoteIP) {
                        processorEvents.add(event(new Integer(connection_id), "new", remoteIP));

                    }

                    public void lostConnectionTemporarily(int connection_id) {
                        processorEvents.add(event(new Integer(connection_id), "lost"));

                    }

                    public void lostConnection(int connection_id) {
                        processorEvents.add(event(new Integer(connection_id), "close"));
                    }

                };
    }

    private final class ClientThread extends Thread {
        private final ClientSocket socket;
        private boolean failed;

        private ClientThread(ClientSocket socket) {
            this.socket = socket;
            this.setDaemon(true);
            this.start();
        }
        public void run() {
            Random random = new Random();
            try {
                for (int i =0; i < 10 && !failed; i++)  {
                    String firstString = "Hello"+System.identityHashCode(this);
                    socket.writeObject(firstString);
                    Thread.sleep(random.nextInt(300)+1);
                    String sndString = "Bye"+System.identityHashCode(this);
                    socket.writeObject(sndString);
                    Thread.sleep(random.nextInt(300)+1);
                    String thdString = "Again"+System.identityHashCode(this);
                    socket.writeObject(thdString);
                    Thread.sleep(random.nextInt(300)+1);
                    Object result1 = socket.readObject();
                    Object result2 = socket.readObject();
                    Object result3 = socket.readObject();
                    failed |= !(firstString+firstString).equals(result1);
                    failed |= !(sndString+sndString).equals(result2);
                    failed |= !(thdString+thdString).equals(result3);
                }
            } catch (Exception e) {
                failed = true;
            }
        }

        public boolean isFailed() {
            return failed;
        }
    }
}