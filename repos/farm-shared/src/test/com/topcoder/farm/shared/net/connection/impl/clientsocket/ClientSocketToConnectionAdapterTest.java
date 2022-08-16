/*
 * ClientSocketToConnectionAdapterTest
 * 
 * Created 07/12/2006
 */
package com.topcoder.farm.shared.net.connection.impl.clientsocket;

import java.io.EOFException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.topcoder.farm.shared.net.connection.api.Connection;
import com.topcoder.farm.shared.net.connection.api.ConnectionFactory;
import com.topcoder.farm.shared.net.connection.api.ConnectionHandler;
import com.topcoder.farm.test.integ.IntegConstants;
import com.topcoder.farm.test.serialization.SerializableCSHandlerFactory;
import com.topcoder.netCommon.io.ClientSocket;

/**
 * Test case for the ClientSocketToConnectionAdapter class
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ClientSocketToConnectionAdapterTest {
    public volatile int closed;
    public volatile int lost;
    public volatile int received;
    public volatile int sent;
    
    private volatile ServerSocket server;
    private volatile Socket serverSocket;
    private Thread serverThread;
    private ConnectionFactory factory;

    @Before
    public void setUp() throws Exception {
        factory = new ClientSocketConnectionFactory(new InetSocketAddress("127.0.0.1" , IntegConstants.CONTROLLER_CLIENT_PORT), 
                                                    new SerializableCSHandlerFactory(), 100000, 10000);
        closed = lost = 0;
        received = sent = 0;
        server = new ServerSocket(IntegConstants.CONTROLLER_CLIENT_PORT);
        serverThread = new Thread() {
            public void run() {
                while (!isInterrupted()) {
                    try {
                        serverSocket = server.accept();
                        ClientSocket clSck = new ClientSocket(serverSocket, new SerializableCSHandlerFactory().newInstance());
                        Integer i = new Integer(0);
                        while (!isInterrupted() && i.intValue() != -1) {
                            i = (Integer) clSck.readObject();
                            sent++;
                            if (i.intValue() == 2) {
                                clSck.writeObject(new Integer(5));
                            }
                        }
                    } catch (EOFException e) {
                        try {
                            serverSocket.close();
                        } catch (IOException e1) {
                          //Ignore
                        }
                    } catch (IOException e) {
                    }
                }
            };
        };
        serverThread.start();
        Thread.sleep(1000);
        
    }

    @After
    public void tearDown() throws Exception {
        serverThread.interrupt();
        if (serverSocket != null) serverSocket.close();
        server.close();
        // need to sleep before connecting again b/c of address in use exception on ec2
        Thread.sleep(5000);
    }
    
    /**
     * When the connection is closed by peer, the connection is reported
     * as lost properly
     */
    @Test
    public void testLostConnectionReportedAndHandled() throws Exception {
       Connection conn = factory.create(buildListener());
       serverSocket.close();
       Thread.sleep(100);
       Assert.assertTrue(closed == 0);
       Assert.assertTrue(lost == 1);
       Assert.assertTrue(conn.isClosed());
       Assert.assertTrue(conn.isLost());
   }
    
    
    
    /**
     * When the connection is closed using the close method, It is reported
     * as closed properly
     */
    @Test
    public void testCloseConnectionReportedAndHandled() throws Exception {
       Connection conn = factory.create(buildListener());
       conn.close();
       Thread.sleep(100);
       Assert.assertTrue(closed == 1);
       Assert.assertTrue(lost == 0);
       Assert.assertTrue(conn.isClosed());
       Assert.assertFalse(conn.isLost());
   }
    
    /**
     * When the connection is closed using the close method and after that the peer drops
     * the connection it is still reported as closed propertly  
     */
    @Test
    public void testCloseAndLost() throws Exception {
        Connection conn = factory.create(buildListener());
        conn.close();
        serverSocket.close();
        Thread.sleep(200);
        Assert.assertTrue(closed == 1);
        Assert.assertTrue(lost == 0);
        Assert.assertTrue(conn.isClosed());
        Assert.assertFalse(conn.isLost());
    }
    
    /**
     * Test that when connected, messages sent through the connection arrive to the peer 
     * and messages sent by peer are received by the connection and notified properly
     */
    @Test
    public void testSendAndReceived() throws Exception {
        Connection conn = factory.create(buildListener());
        conn.send(new Integer(1));
        Thread.sleep(100);
        Assert.assertTrue(sent == 1);
        conn.send(new Integer(2));
        Thread.sleep(100);
        Assert.assertTrue(sent == 2);
        Assert.assertTrue(received == 1);
        serverSocket.close();
    }
    
    private ConnectionHandler buildListener() {
        return new ConnectionHandler() {
        
            public void connectionLost(Connection connection) {
                lost++;
            }
        
            public void connectionClosed(Connection connection) {
                closed++;
            }
        
            public void receive(Connection connection, Object message) {
                received++;
            }
        };
    }
}
