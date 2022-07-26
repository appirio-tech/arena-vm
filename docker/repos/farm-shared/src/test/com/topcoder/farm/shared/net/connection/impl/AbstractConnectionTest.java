/*
 * AbstractConnectionTest
 * 
 * Created 07/11/2006
 */
package com.topcoder.farm.shared.net.connection.impl;

import junit.framework.TestCase;

import com.topcoder.farm.shared.net.connection.api.Connection;
import com.topcoder.farm.shared.net.connection.api.ConnectionHandler;

/**
 * Test Case for class AbstractConnection
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class AbstractConnectionTest extends TestCase {
    private int hndLostCnt;
    private int hndClosedCnt;
    private int hndReceivedCnt;
    private TestConnection cnn;
    
    protected void setUp() throws Exception {
        hndLostCnt = 0;
        hndClosedCnt = 0;
        hndReceivedCnt = 0;
        
        cnn = new TestConnection();
        cnn.setHandler(new ConnectionHandler() {
            public void connectionLost(Connection connection) {
                hndLostCnt++;
        
            }
        
            public void connectionClosed(Connection connection) {
                hndClosedCnt++;
            }
        
            public void receive(Connection connection, Object message) {
                hndReceivedCnt++;
            }
        });
    }
    
    /**
     * Tests that the initial state of the connection is not closed neither lost
     */
    public void testInitiallyIsConnected() throws Exception {
        assertFalse(cnn.isClosed());
        assertFalse(cnn.isLost());
    }
    
    /**
     * Tests that when not closed, send ends up in bareSend
     */
    public void testBareSendReachedWhenConnected() throws Exception {
        cnn.send(new Object());
        assertTrue(1 ==  cnn.bareSendCnt);
    }

    /**
     * Tests that when close is invoke the bareClose gets called, the connection 
     * state is set as closed, but the handler is not called
     */
    public void testBareCloseReachedWhenConnected() throws Exception {
        cnn.close();
        assertTrue(1 ==  cnn.bareCloseCnt);
        assertTrue(cnn.isClosed());
        assertFalse(cnn.isLost());
        assertTrue(0 ==  hndClosedCnt);
        assertTrue(0 == hndLostCnt);
    }

    /**
     * Tests that handleReceived notifies the handler. 
     */
    public void testReceivedWhenConnected() throws Exception {
        cnn.handleReceived(null);
        assertTrue(1 == hndReceivedCnt);
    }

    /**
     * Tests that handleConnectionClosed notifies to the handler that 
     * the connection has been closed, and the state of the connection reflects the 
     * close state
     */
    public void testHandleClosedWhenConnected() throws Exception {
        cnn.handleConnectionClosed();
        assertTrue(cnn.isClosed());
        assertFalse(cnn.isLost());
        assertTrue(1 ==  hndClosedCnt);
        assertTrue(0 == hndLostCnt);
    }

    /**
     * Tests that after close, send is not allowed
     */
    public void testNotSendWhenClosing() throws Exception {
        cnn.close();
        try {
            cnn.send(new Object());
            fail("Expected exception");
        } catch (Exception e) {
        }
        assertTrue(cnn.bareSendCnt == 0);
    }
    
    /**
     * Tests that handleConnectionLost notifies the handler.
     * and set the connection state as Lost 
     */
    public void testLostConnection() throws Exception {
        cnn.handleConnectionLost();
        assertTrue(cnn.isClosed());
        assertTrue(cnn.isLost());
        assertTrue(0 == hndClosedCnt);
        assertTrue(1 == hndLostCnt);
    }

    /**
     * Tests that when the connection has been lost, send is not allowed
     */
    public void testNotSendAfterLost() throws Exception {
        cnn.handleConnectionLost();
        try {
            cnn.send(new Object());
            fail("Expected exception");
        } catch (Exception e) {
        }
        assertTrue(cnn.bareSendCnt == 0);
    }
    
    /**
     * Tests that when the connection has been lost, if a closing is reported
     * it is not notified to the handler and the state is still lost
     */
    public void testNotCloseNotifiedAfterLost() throws Exception {
        cnn.handleConnectionLost();
        cnn.handleConnectionClosed();
        assertTrue(hndClosedCnt == 0);
        assertTrue(cnn.isLost());
    }
    
    
    /**
     * Tests that when the connection has been lost, if a closed is reported
     * it is not notified to the handler
     */
    public void testNotLostNotifiedAfterClose() throws Exception {
        cnn.handleConnectionClosed();
        cnn.handleConnectionLost();
        assertTrue(hndLostCnt == 0);
        assertTrue(!cnn.isLost());
    }
    
    
    /**
     * Tests that during the closing received object are notified to the handler 
     */
    public void testClosingNotifiesReceived() throws Exception {
        cnn.close();
        cnn.handleReceived(cnn);
        assertTrue(1 == hndReceivedCnt);
        
    }

    /**
     * Tests that during the closing received object are notified to the handler 
     */
    public void testClosedNotNotifyReceived() throws Exception {
        cnn.close();
        cnn.handleConnectionClosed();
        cnn.handleReceived(cnn);
        assertTrue(0 == hndReceivedCnt);
    }
    
    /**
     * Tests after the connection has been reported as lost, received object are not notified 
     * to the handler 
     */
    public void testLostNotNotifyReceived() throws Exception {
        cnn.handleConnectionLost();
        cnn.handleReceived(cnn);
        assertTrue(0 == hndReceivedCnt);
    }

    private static class TestConnection extends AbstractConnection {
        volatile int bareCloseCnt = 0;
        volatile int bareSendCnt = 0; 
        
        public void bareSend(Object object) {
            bareSendCnt++;
        }

        public void bareClose() {
            bareCloseCnt++;
        }
    }
}
