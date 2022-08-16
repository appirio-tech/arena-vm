/*
 * HTTPTunnelListenerExceptionHandlingTest
 *
 * Created 04/13/2007
 */
package com.topcoder.net.httptunnel.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import junit.framework.TestCase;

import com.topcoder.net.httptunnel.common.digest.TokenDigester;
import com.topcoder.server.listener.ListenerInterface;
import com.topcoder.server.listener.ProcessorInterface;
import com.topcoder.server.listener.monitor.MonitorInterface;
import com.topcoder.shared.netCommon.CSHandler;
import com.topcoder.shared.netCommon.CSHandlerFactory;

/**
 * Basic unit test to verify HTTPTunnelListener exception handling.
 *
 *
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class HTTPTunnelListenerExceptionHandlingTest extends TestCase {
    private List processorEvents;
    private HTTPTunnelListener listener;
    private ProcessorInterface processor;
    private List monitorEvents;
    private SocketReadThread plainSocket;

    protected void setUp() throws Exception {
        processorEvents = Collections.synchronizedList(new ArrayList());
        monitorEvents = Collections.synchronizedList(new ArrayList());
        processor = buildProcessor();
        MonitorInterface monitor = buildMonitor();
        listener = new HTTPTunnelListener(null, 8080, processor, 2, 2, 2, monitor,
                new CSHandlerFactory() {
                    public CSHandler newInstance() {
                        return new CSHandler() {
                            protected boolean writeObjectOverride(Object object) throws IOException {
                                return false;
                            }
                        };
                    }
                }, new HashSet(), false, 0, Integer.MAX_VALUE);
    }

    protected void tearDown() throws Exception {
        listener.stop();
        if (plainSocket != null) {
            plainSocket.close();
        }
        dump();
    }



    public void testCreateTunnelAndTryToHijackTunnel() throws Exception {
        startListener();
        writeToSocket(plainSocket, "GET tunnel HTTP/1.1\r\n\r\n");
        Thread.sleep(200);
        long tcTS = System.currentTimeMillis();
        SocketReadThread s2 = newSocket();
        try {
            writeToSocket(s2, "GET tunnel?id="+listener.getMinConnectionId()+" HTTP/1.1\r\n" +
                    "TC-TS: "+tcTS+"\r\n"+
                    "TC-Digest: " + new TokenDigester().generateDigest(""+new Random().nextLong(), listener.getMinConnectionId(), ""+tcTS)+
                    "\r\n\r\n");
            Thread.sleep(200);
            checkProcessorEvents("start,new");
            checkMonitorEvents("new,new,lost", true);
            checkSocketEvents(plainSocket, "read");
            checkSocketEvents(s2, "lost");
        } finally {
            s2.close();
        }
    }

    public void testCloseIfMessageIsNotRecognized() throws Exception {
        startListener();
        writeToSocket(plainSocket, "trash to write");
        Thread.sleep(200);
        checkProcessorEvents("start");
        checkMonitorEvents("new,lost", true);
        checkSocketEvents(plainSocket, "lost");
    }


    private void writeToSocket(SocketReadThread s, Object o) throws IOException {
        s.getOutputStream().write(o.toString().getBytes());
    }

    private void writeToSocket(SocketReadThread s, byte[] o) throws IOException {
        s.getOutputStream().write(o);
    }

    public void testMethodOkAndContentLengthOverFlow() throws Exception {
        startListener();
        writeToSocket(plainSocket, "GET / HTTP/1.1\r\n");
        writeToSocket(plainSocket, HTTPConstants.HEADER_CONTENT_LENGTH);
        writeToSocket(plainSocket, ": 27000000\r\n\r\n");
        byte[] bytes = "012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789\n".getBytes();
        try {
            for (int i = 0; i < 10 ; i++) {
                writeToSocket(plainSocket, bytes);
            }
        } catch (Exception e) {
        }
        Thread.sleep(200);
        checkProcessorEvents("start");
        checkMonitorEvents("new,lost", true);
        checkSocketEvents(plainSocket, "lost");
    }

    public void testCreateTunnelAndSendOnConnection() throws Exception {
        startListener();
        writeToSocket(plainSocket, "GET tunnel HTTP/1.1\r\n\r\n");
        Thread.sleep(200);
        writeToSocket(plainSocket, "GET tunnel HTTP/1.1\r\n\r\n");
        Thread.sleep(200);
        checkProcessorEvents("start,new,lost");
        checkMonitorEvents("new,lost", true);
        checkSocketEvents(plainSocket, "read,lost");
    }




    public void testMethodOkButBufferOverFlows() throws Exception {
        startListener();
        writeToSocket(plainSocket, "GET HHHH\n");
        byte[] bytes = "012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789\n".getBytes();
        try {
            for (int i = 0; i < 1000000 ; i++) {
                writeToSocket(plainSocket,bytes);
            }
        } catch (Exception e) {
        }
        Thread.sleep(200);
        checkProcessorEvents("start");
        checkMonitorEvents("new,lost", true);
        checkSocketEvents(plainSocket, "lost");
    }




    private void startListener() throws Exception {
        Thread.sleep(200);
        listener.start();
        Thread.sleep(600);
        plainSocket = newSocket();
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

    private void checkSocketEvents(SocketReadThread s, String events) {
        String[] evs = events.split(",");
        int i = 0;
        List socketEvents = s.getSocketEvents();
        assertEquals(evs.length, socketEvents.size());
        for (Iterator it = socketEvents.iterator(); it.hasNext(); i++) {
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

    private SocketReadThread newSocket() throws IOException {
        SocketReadThread s = new SocketReadThread(new Socket(InetAddress.getLocalHost().getHostAddress(), 8080));
        return s;
    }

    protected List event(Integer id, String type, Object object) {
        return Arrays.asList(new Object[] {id, type, object});
    }

    protected List event(Integer id, String type) {
        return Arrays.asList(new Object[] {id, type, null});
    }

    private MonitorInterface buildMonitor() {
        return new MonitorInterface() {
            public void newConnection(int id, String remoteIP) {
                monitorEvents.add(event(new Integer(id), "new", remoteIP));
            }

            public void lostConnection(int id) {
                monitorEvents.add(event(new Integer(id), "lost"));
            }

            public void bytesRead(int id, int numBytes) {
                monitorEvents.add(event(new Integer(id), "read", new Integer(numBytes)));
            }

            public void associateConnections(int existentConnectionId, int newConnectionID) {
                monitorEvents.add(event(new Integer(existentConnectionId), "associateConnections", new Integer(newConnectionID)));
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


    private class SocketReadThread extends Thread {
        private Socket socket;
        private List socketEvents = new ArrayList();
        private OutputStream os;
        private volatile boolean closed;

        public SocketReadThread(Socket socket) {
            this.socket = socket;
            this.setDaemon(true);
            this.start();
        }

        public void close() throws IOException {
            this.closed = true;
            this.socket.close();
        }

        public OutputStream getOutputStream() throws IOException {
            if (os == null) {
                os = socket.getOutputStream();
            }
            return os;
        }

        public void run() {
            try {
                InputStream is = socket.getInputStream();
                while (true) {
                    byte[]  buffer = new byte[200000];
                    int read = is.read(buffer);
                    if (read < 0) {
                        break;
                    }
                    socketEvents.add(event(null, "read", new String(buffer, 0, read)));
                }
            } catch (Exception e) {
            }
            if (!closed) {
                socketEvents.add(event(null, "lost", null));
            } else {
                socketEvents.add(event(null, "closed", null));
            }
        }

        protected Socket getSocket() {
            return socket;
        }

        protected List getSocketEvents() {
            return socketEvents;
        }

        public String toString() {
            return socketEvents.toString();
        }

    }

}