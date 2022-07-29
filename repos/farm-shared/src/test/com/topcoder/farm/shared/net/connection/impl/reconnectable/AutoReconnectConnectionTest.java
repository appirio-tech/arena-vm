/*
 * AutoReconnectConnectionTest
 * 
 * Created 07/14/2006
 */
package com.topcoder.farm.shared.net.connection.impl.reconnectable;

import java.io.IOException;

import junit.framework.TestCase;

import com.topcoder.farm.shared.net.connection.api.Connection;
import com.topcoder.farm.shared.net.connection.api.ConnectionFactory;
import com.topcoder.farm.shared.net.connection.api.ConnectionHandler;
import com.topcoder.farm.shared.net.connection.api.ReconnectableConnection;
import com.topcoder.farm.shared.net.connection.api.ReconnectableConnectionHandler;
import com.topcoder.farm.shared.net.connection.mock.MockConnection;
import com.topcoder.farm.shared.net.connection.mock.MockConnectionFactory;

/**
 * Test case for AutoReconnectConnection class
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class AutoReconnectConnectionTest extends TestCase {
    private static final int TIME_BT_ATTEMPTS = 300;
    private volatile int closed;
    private volatile int lost;
    private volatile int received;
    private volatile int messageAsked;
    private volatile int reconnected;
    private volatile int reconnecting;

    private ConnectionFactory factory;
    private MockConnectionFactory mockFactory;

    protected void setUp() throws Exception {
        received =closed = lost = 0;
        mockFactory = new MockConnectionFactory();
        factory = new AutoReconnectConnectionFactory(mockFactory, 3, TIME_BT_ATTEMPTS);
    }

    protected void tearDown() throws Exception {
    }

    /**
     * When the connection is closed by peer, the connection is reported
     * as lost properly
     */
    public void testSendTwoTimesAndClosedHandled() throws Exception {
        AutoReconnectConnection conn = (AutoReconnectConnection) factory.create(buildListener());
        MockConnection mock = getMockConn(conn);
        conn.send("1");
        conn.send("2");
        conn.close();
        assertTrue(mock.isClosingFlag());
        mock.forceClosed();
        Thread.sleep(200);
        assertTrue(reconnected == 0);
        assertTrue(reconnecting == 0);
        assertTrue(mock.getSentObjects().size() == 2);
        isClosedOnly(conn);
    }

    /**
     * When the internal connection is reported as lost,
     * a new connection is created and events are reported properly
     * to the handler. (reconnecting and reconnected)
     */
    public void testLostHandled() throws Exception {
        AutoReconnectConnection conn = (AutoReconnectConnection) factory.create(buildListener());
        MockConnection initMock = getMockConn(conn);
        
        //We force the underlying connection as lost
        initMock.forceLost();
        Thread.sleep(200);
        //The new underlying connection has changed
        MockConnection mock = getMockConn(conn);

        assertTrue(mock != initMock);

        //The events were reported
        assertTrue(reconnected == 1);
        assertTrue(reconnecting == 1);

        //The new connection contains the reconnected message object 
        //but the previous don't
        assertTrue(mock.getSentObjects().size() == 1);
        assertTrue(initMock.getSentObjects().size() == 0);
        isOpen(conn);

        //Now the we close the connection and look that it is properly closed
        conn.close();
        assertTrue(mock.isClosingFlag());
        mock.forceClosed();
        Thread.sleep(200);
        isClosedOnly(conn);
    }


    /**
     * When the internal connection is reported as lost, and the a new connection
     * cannot be created the connection is reported as lost properly
     */
    public void testLostAndCouldNotReconnectHandled() throws Exception {
        AutoReconnectConnection conn = (AutoReconnectConnection) factory.create(buildListener());
        MockConnection initMock = getMockConn(conn);
        mockFactory.setThrowIOException(true);
        initMock.forceLost();
        Thread.sleep(4*TIME_BT_ATTEMPTS);
        assertTrue(reconnecting == 1);
        assertTrue(reconnected == 0);
        assertTrue(mockFactory.getInvocationCount() == 4);
        assertTrue(mockFactory.getConnectionCount() == 1);
        isLost(conn);
    }

    /**
     * When the internal connection is reported as lost, new connections
     * are created until the reconnect attempt succeeds or fails.
     * If 2 new connections are lost during the reconnection attempt
     * but the third one succeeds, the connection is reported as reconnected
     */
    public void testLostAnd2ReLostDuringReconnect() throws Exception {
        addConnection();
        addLostOnSendConnection();
        addLostOnSendConnection();

        AutoReconnectConnection conn = (AutoReconnectConnection) factory.create(buildListener());
        MockConnection initMock = getMockConn(conn);
        initMock.forceLost();
        Thread.sleep(4*TIME_BT_ATTEMPTS);
        assertEquals(reconnecting,  reconnected);
        assertTrue(mockFactory.getInvocationCount() == 4);
        assertTrue(mockFactory.getConnectionCount() == 4);
        assertTrue(messageAsked == 3);
        isOpen(conn);
        conn.close();
        getMockConn(conn).forceClosed();
        Thread.sleep(200);
        isClosedOnly(conn);
    }


    /**
     * When the internal connection is reported as lost, new connections
     * are created until the reconnect attempt succeeds or fails.
     * If the 3 new connections are lost during the reconnection attempt
     * the connection is reported as lost
     */
    public void testLostAnd3ReLostDuringReconnect() throws Exception {
        //Configure the factory to return one ordinary mock, 2 lostOnSend Mock, and Finally on mock 
        addConnection();
        addLostOnSendConnection();
        addLostOnSendConnection();
        addLostOnSendConnection();

        AutoReconnectConnection conn = (AutoReconnectConnection) factory.create(buildListener());

        MockConnection initMock = getMockConn(conn);
        initMock.forceLost();
        Thread.sleep(5*TIME_BT_ATTEMPTS+1000);
        
        System.out.println(lost);
        assertTrue(reconnecting == reconnected + 1);
        assertTrue(mockFactory.getInvocationCount() == 4);
        assertTrue(mockFactory.getConnectionCount() == 4);
        assertTrue(messageAsked == 3);
        isLost(conn);
    }

    /**
     * During the reconnection attempt a message is sent. If the sending fails
     * a new reconnection attempt is made. This test forces 2 exception during 
     * the sending and the final reconnection succeed
     */
    public void testLostAnd2ExceptionDuringSendOnReconnect() throws Exception {
        //Configure the factory to return one ordinary mock, 2 lostOnSend Mock, and Finally one ordinary mock 
        addConnection();
        addExceptionOnSendConnection();
        addExceptionOnSendConnection();

        AutoReconnectConnection conn = (AutoReconnectConnection) factory.create(buildListener());

        MockConnection initMock = getMockConn(conn);
        initMock.forceLost();
        Thread.sleep(4*TIME_BT_ATTEMPTS);

        //Check that the reconnection attempt succedeed, but 4 connections where created and 
        //3 reconnection messages were required 
        assertTrue(reconnecting == 1);
        assertTrue(reconnected == 1);
        assertTrue(mockFactory.getInvocationCount() == 4);
        assertTrue(mockFactory.getConnectionCount() == 4);
        assertTrue(messageAsked == 3);
        isOpen(conn);
    }

    
    /**
     * During the reconnection attempt a message is sent. If the sending fails
     * a new reconnection attempt is made. This test forces 3 exception during 
     * the sending so the connection is reported as lost
     */
    public void testLostAnd3ExceptionDuringSendOnReconnect() throws Exception {
        //Configure the factory to return one ordinary mock, 2 lostOnSend Mock, and Finally on mock 
        addConnection();
        addExceptionOnSendConnection();
        addExceptionOnSendConnection();
        addExceptionOnSendConnection();

        AutoReconnectConnection conn = (AutoReconnectConnection) factory.create(buildListener());

        MockConnection initMock = getMockConn(conn);
        initMock.forceLost();
        Thread.sleep(4*TIME_BT_ATTEMPTS);

        //Checks that the connection is lost, 4 connection were created and 3 messages required
        assertTrue(reconnecting == 1);
        assertTrue(reconnected == 0);
        assertTrue(mockFactory.getInvocationCount() == 4);
        assertTrue(mockFactory.getConnectionCount() == 4);
        assertTrue(messageAsked == 3);
        isLost(conn);
    }

    private void addLostOnSendConnection() {
        MockConnection lostOnSendConnection = new MockConnection();
        lostOnSendConnection.setLostOnSend(true);
        mockFactory.addConnection(lostOnSendConnection);
    }

    private void addExceptionOnSendConnection() {
        MockConnection lostOnSendConnection = new MockConnection();
        lostOnSendConnection.setExceptionOnSend(true);
        mockFactory.addConnection(lostOnSendConnection);
    }
    
    private void addConnection() {
        mockFactory.addConnection(new MockConnection());
    }


    private MockConnection getMockConn(AutoReconnectConnection conn) {
        return (MockConnection) conn.getConnection();
    }


    /**
     * Checks that the connection is not closed but not lost
     */
    private void isClosedOnly(AutoReconnectConnection conn) {
        assertTrue(closed == 1);
        assertTrue(lost == 0);
        assertTrue(conn.isClosed());
        assertFalse(conn.isLost());
    }



    /**
     * Checks that the connection is not closed but not lost
     */
    private void isLost(AutoReconnectConnection conn) {
        assertTrue(closed == 0);
        assertTrue(lost == 1);
        assertTrue(conn.isClosed());
        assertTrue(conn.isLost());
    }


    /**
     * Checks that the connection is not closed neither lost
     */
    private void isOpen(AutoReconnectConnection conn) {
        assertTrue(closed == 0);
        assertTrue(lost == 0);
        assertFalse(conn.isClosed());
        assertFalse(conn.isLost());
    }

    private ConnectionHandler buildListener() {
        return new ReconnectableConnectionHandler() {
            public void connectionLost(Connection connection) {
                lost++;
            }

            public void connectionClosed(Connection connection) {
                closed++;
            }

            public void receive(Connection connection, Object message) {
                received++;
            }

            public void performReconnection(ReconnectableConnection connection) throws IOException {
                messageAsked++;
                connection.send(new Integer(reconnecting));
            }

            public void reconnected(ReconnectableConnection connection) {
                reconnected++;

            }

            public void reconnecting(ReconnectableConnection connection) {
                reconnecting++;

            }
        };
    }
}
