/*
 * NBIOListenerToConnectionListenerAdapterTest
 * 
 * Created 07/17/2006
 */
package com.topcoder.farm.server.net.connection.listener;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.AssertionFailedError;

import com.topcoder.farm.server.net.connection.ConnectionListenerHandler;
import com.topcoder.farm.shared.net.connection.api.Connection;
import com.topcoder.farm.shared.net.connection.api.ConnectionHandler;
import com.topcoder.farm.shared.net.connection.impl.clientsocket.ClientSocketConnectionFactory;
import com.topcoder.farm.test.integ.IntegConstants;
import com.topcoder.farm.test.serialization.SerializableCSHandlerFactory;
import com.topcoder.farm.test.util.MTTestCase;
import com.topcoder.server.listener.monitor.EmptyMonitor;

/**
 * Test Case for NBIOListenerToConnectionListenerAdapter class
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class NBIOListenerToConnectionListenerAdapterTest extends MTTestCase {
    private ClientSocketConnectionFactory clientFactory = null; 
    private List<TestEvent> serverEvents;
    private List<TestEvent> clientEvents;
    private Map mapServer;
    private Map mapClient;
    
    public NBIOListenerToConnectionListenerAdapterTest() throws UnknownHostException {
        clientFactory = new ClientSocketConnectionFactory(new InetSocketAddress(InetAddress.getLocalHost(), 
                            IntegConstants.CONTROLLER_CLIENT_PORT), new SerializableCSHandlerFactory(), 100000, 100000);
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        serverEvents = Collections.synchronizedList(new ArrayList<TestEvent>());
        clientEvents = Collections.synchronizedList(new ArrayList<TestEvent>());
    }
    
    protected void tearDown() throws Exception {
        super.tearDown();
        serverEvents = null;
        clientEvents = null;
    }

    /**
     * If the start method is invoked on the listener before a handler has been set
     * an exception should be thrown
     */
    public void testStartWithoutHandler() throws Exception {
        NBIOListenerToConnectionListenerAdapter listener = buildListenerAdapter();
        try {
            listener.start();
            fail("Expected Exception, not handler set");
        } catch (Exception e) {
        }
    }

    /**
     * Test event sequence, on client side and server side
     * The client sends 2 echo messages to the server and then closes the connection. 
     */
    public void testCloseOnClientAndLostOnServer() throws Exception {
        NBIOListenerToConnectionListenerAdapter listener = startListener();
        try {
            run(build2SendAndClose());

            startAllAndWait();
            
            //Expected client events
            checkEvents(clientEvents, 0, "received", "message1");
            checkEvents(clientEvents, 1, "received", "message2");
            checkEvents(clientEvents, 2, "closed", null);

            //Expected server events
            checkEvents(serverEvents, 0, "received", "message1");
            checkEvents(serverEvents, 1, "received", "message2");
            checkEvents(serverEvents, 2, "lost", null);

        } finally {
            listener.stop();
        }
    }
    
    
    /**
     * Test event sequence, on client side and server side
     * The client sends 2 echo messages to the server and then a third message
     * to force the server to close the connection
     */
    public void testCloseOnServerAndLostOnClient() throws Exception {
        NBIOListenerToConnectionListenerAdapter listener = startListener();
        try {
            run(build2SendAndLost());
            
            startAllAndWait();
            
            //Expected client events
            checkEvents(clientEvents, 0, "received", "message1");
            checkEvents(clientEvents, 1, "received", "message2");
            checkEvents(clientEvents, 2, "lost", null);

            //Expected server events
            checkEvents(serverEvents, 0, "received", "message1");
            checkEvents(serverEvents, 1, "received", "message2");
            checkEvents(serverEvents, 2, "received", "close");
            checkEvents(serverEvents, 3, "closed", null);
        } finally {
            listener.stop();
        }
    }

    /**
     * Test event sequence, on server side and client sides
     * This test uses 2 clients simultaneously.
     */
    public void testMTCloseOnClientAndLostOnServer() throws Exception {
        NBIOListenerToConnectionListenerAdapter listener = startListener();
        try {
            run(build2SendAndClose());
            run(build2SendAndClose());
            startAllAndWait();

            mapServer = buildMap(serverEvents);
            mapClient = buildMap(clientEvents);
            
            checkAllEvents(mapClient, 0, "received", "message1");
            checkAllEvents(mapClient, 1, "received", "message2");
            checkAllEvents(mapClient, 2, "closed", null);

            checkAllEvents(mapServer, 0, "received", "message1");
            checkAllEvents(mapServer, 1, "received", "message2");
            checkAllEvents(mapServer, 2, "lost", null);

        } finally {
            listener.stop();
        }
    }
    
    /**
     * Builds a Runnable that creates a client connection, sends 2 echo messages and
     * a closes message
     * 
     * @return the built Runnable
     */
    private Runnable build2SendAndLost() {
        return new Runnable() {
            public void run() {
                try {
                    Connection cnn = clientFactory.create(new ClientHandler());
                    cnn.send("message1");
                    cnn.send("message2");
                    cnn.send("close");
                    Thread.sleep(400);
                } catch (Exception e) {
                    e.printStackTrace();
                    fail("Unexpected exception");
                }
            }
        };
    }

    /**
     * Builds a Runnable that creates a client connection, sends 2 echo messages and
     * closes the connection
     * 
     * @return the built Runnable
     */
    private Runnable build2SendAndClose() {
        return new Runnable() {
            public void run() {
                try {
                    Connection cnn = clientFactory.create(new ClientHandler());
                    cnn.send("message1");
                    cnn.send("message2");
                    Thread.sleep(200);
                    cnn.close();
                    Thread.sleep(200);
                } catch (Exception e) {
                    e.printStackTrace();
                    fail("Unexpected exception");
                }
            }
        };
    }

    /**
     * Starts the Listener.
     * 
     * @return the created Listener
     */
    private NBIOListenerToConnectionListenerAdapter startListener() throws IOException, InterruptedException {
        NBIOListenerToConnectionListenerAdapter listener = buildListenerAdapter();
        listener.setHandler(new ConnectionListenerHandler() {
            public void newConnection(Connection connection) {
                connection.setHandler(new ServerHandler());
            }
        });
        listener.start();
        Thread.sleep(500);
        return listener;
    }
    
    /**
     * Creates a NBIOListenerToConnectionListenerAdapter with a fixed configuration
     * 
     * @return the created Listener
     */
    private NBIOListenerToConnectionListenerAdapter buildListenerAdapter() {
        return new NBIOListenerToConnectionListenerAdapter(IntegConstants.CONTROLLER_CLIENT_PORT, 2, 2, 2, 
                        new EmptyMonitor(), new SerializableCSHandlerFactory(), new ArrayList(), false, 50000000, 5000000);
    }
    
    /**
     * Checks that the event located at index evIndex on the evs List matchs 
     * the given eventName and extra value
     * 
     * @param evs List of events
     * @param evIndex Event Index
     * @param eventName Name of the Event
     * @param extra Extra value
     */
    private void checkEvents(List<TestEvent> evs, int evIndex, String eventName, Object extra) {
        TestEvent event = evs.get(evIndex);
        assertEquals(eventName, event.eventName);
        assertEquals(extra, event.extra);
    }
    
    
    private void checkAllEvents(Map mapEvents, int evIndex, String name, Object extra) {
        for (Iterator it = mapEvents.entrySet().iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            TestEvent event = ((List<TestEvent>) entry.getValue()).get(evIndex);
            try {
                assertEquals(name, event.eventName);
                assertEquals(extra, event.extra);
            } catch (AssertionFailedError e) {
                System.out.println("Failed for connection " + entry.getKey());
                throw e;
            }
        }
    }

    /**
     * Builds a map <Connection, List<TestEvent>> from the given list of events
     * 
     * @param evs List of events to process
     * 
     * @return the built Map
     */
    private Map buildMap(List<TestEvent> evs) {
        Map mappedEvents = new HashMap();
        for (TestEvent event : evs) {
            List<TestEvent> l = (List<TestEvent>) mappedEvents.get(event.connection);
            if (l == null) {
                l = new ArrayList<TestEvent>();
                mappedEvents.put(event.connection, l);
            }
            l.add(event);
        }
        return mappedEvents;
    }

    /**
     * Client ConnectionHandler
     * - Registers events
     */
    private final class ClientHandler implements ConnectionHandler {
        public void receive(Connection connection, Object message) {
            clientEvents.add(new TestEvent(connection, "received", message));
            System.out.println("CLIENT - connection "+connection+" received message "+message);
        }
        public void connectionLost(Connection connection) {
            clientEvents.add(new TestEvent(connection, "lost", null));
            System.out.println("CLIENT - connection "+connection+" lost");
        }
        public void connectionClosed(Connection connection) {
            clientEvents.add(new TestEvent(connection, "closed", null));
            System.out.println("CLIENT - connection "+connection+" closed");        }
    }


    /**
     * Server ConnectionHandler
     * - Registers events
     * - Echoes messages
     * - "close" message close connection
     */
    private final class ServerHandler implements ConnectionHandler {
        public void receive(Connection connection, Object message) {
            serverEvents.add(new TestEvent(connection, "received", message));
            System.out.println("SERVER - connection "+connection+" received message "+message);
            try {
                if (message.equals("close")) {
                    Thread.sleep(300);
                    connection.close();
                } else {
                    connection.send(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
                fail("Unexpected exception on server");
            }
        }
        public void connectionLost(Connection connection) {
            serverEvents.add(new TestEvent(connection, "lost", null));
            System.out.println("SERVER - connection "+connection+" lost");
        }
        public void connectionClosed(Connection connection) {
            serverEvents.add(new TestEvent(connection, "closed", null));
            System.out.println("SERVER - connection "+connection+" closed");
        }
    }


    /**
     * Value Object to register events on connections
     */
    private static class TestEvent {
        public Connection connection;
        public String eventName;
        public Object extra;
        
        public TestEvent(Connection connection, String eventName, Object extra) {
            this.connection = connection;
            this.eventName = eventName;
            this.extra = extra;
        }
        
        public String toString() {
            return eventName + "\n" + connection + "\n" + extra + "\n";
        }
    }
}
