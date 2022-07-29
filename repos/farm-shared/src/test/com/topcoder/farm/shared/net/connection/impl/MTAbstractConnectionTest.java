/*
 * MTAbstractConnectionTest
 * 
 * Created 07/11/2006
 */
package com.topcoder.farm.shared.net.connection.impl;

import java.io.IOException;

import com.topcoder.farm.shared.net.connection.api.Connection;
import com.topcoder.farm.shared.net.connection.api.ConnectionHandler;
import com.topcoder.farm.test.util.MTTestCase;


/**
 * Concurrent Test case for class AbstractConnection
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class MTAbstractConnectionTest extends MTTestCase  {
    private volatile int hndLostCnt;
    private volatile int hndClosedCnt;
    private volatile int hndReceivedCnt;
    private TestConnection cnn;
    
    protected void setUp() throws Exception {
        super.setUp();
        hndLostCnt = 0;
        hndClosedCnt = 0;
        hndReceivedCnt = 0;

        cnn = new TestConnection();
        cnn.setHandler(new ConnectionHandler() {
            public synchronized void connectionLost(Connection connection) {
                hndLostCnt++;
            }
        
            public synchronized void connectionClosed(Connection connection) {
                hndClosedCnt++;
            }
        
            public synchronized void receive(Connection connection, Object message) {
                hndReceivedCnt++;
            }
        });
    }
    
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    
    /**
     * Tests that while Closing, send throws Exception
     */
    public void testCloseAndSend() throws Exception {
        run(new Runnable() {
            public void run() {
                cnn.close();
            }
        });
        run(new Runnable() {
            public void run() {
                try {
                    cnn.send(new Object());
                    fail("Expected exception");
                } catch (IOException e) {
                }
            }
        });
        startAllAndWait();
        displayState("testCloseAndSend");
    }
    
    
    /**
     * Tests that send first and close after doesn't throw exception,
     * the bareSend gets called and the connection changes state to closing
     */
    public void testSendAndClose() throws Exception {
        run(new Runnable() {
            public void run() {
                try {
                    cnn.send(new Object());
                } catch (IOException e) {
                    fail("UnExpected exception");
                }
            }
        });
        run(new Runnable() {
            public void run() {
                cnn.close();
            }
        });
        startAllAndWait();
        displayState("testSendAndClose");
        assertTrue(isClosing() && cnn.bareSendCnt == 1);
    }
    
    /**
     * Tests that if the connection is reported as lost, closing the connection
     * doesn't affect the state
     */
    public void testLostAndClose() throws Exception {
        run(new Runnable() {
            public void run() {
                cnn.handleConnectionLost();
            }
        });
        run(new Runnable() {
            public void run() {
                cnn.close();
                cnn.handleConnectionClosed();
            }
        });
        startAllAndWait();
        displayState("testLostAndClose");
        assertTrue(isLostReported());
    }
    
    
    /**
     * Tests that if the connection is reported as lost, send through the connection
     * throws exception
     */
    public void testLostAndSend() throws Exception {
        run(new Runnable() {
            public void run() {
                cnn.handleConnectionLost();
            }
        });
        run(new Runnable() {
            public void run() {
                try {
                    cnn.send(new Object());
                    fail("Expected exception");
                } catch (IOException e) {
                }
            }
        });
        startAllAndWait();
        displayState("testLostAndSend");
        assertTrue(isLostReported());
    }
    
    /**
     * Tests that if the connection is closing, a lost report 
     * changes the state to closed
     */
    public void testClosingAndLost() throws Exception {
        run(new Runnable() {
            public void run() {
                cnn.close();
            }
        });
        run(new Runnable() {
            public void run() {
                cnn.handleConnectionLost();
            }
        });
        
        startAllAndWait();
        displayState("testClosingAndLost");
        assertTrue(isClosed());
    }

    /**
     * Tests that if the connection is closed, a lost report 
     * doesn't affect the state
     */
    public void testClosedAndLost() throws Exception {
        run(new Runnable() {
            public void run() {
                cnn.close();
                cnn.handleConnectionClosed();
            }
        });
        run(new Runnable() {
            public void run() {
                cnn.handleConnectionLost();
            }
        });
        
        startAllAndWait();
        displayState("testClosedAndLost");
        assertTrue(isClosed());
    }

    private boolean isClosing() {
        return (hndLostCnt == 0 && hndClosedCnt == 0 && cnn.bareCloseCnt == 1);
    }

    private boolean isLostReported() {
        return (hndLostCnt == 1 && hndClosedCnt == 0 && cnn.bareCloseCnt == 0);
    }
    
    private boolean isClosed() {
        return (hndLostCnt == 0 && hndClosedCnt == 1 && cnn.bareCloseCnt == 1);
    }

    private void displayState(String name) {
        System.out.println(name);
        System.out.println(cnn.getState().getClass());
        System.out.println("hndReceivedCnt="+hndReceivedCnt);
        System.out.println("hndLostCnt="+hndLostCnt);
        System.out.println("hndClosedCnt="+hndClosedCnt);
        System.out.println("cnn.closed="+cnn.bareCloseCnt);
        System.out.println("cnn.send="+cnn.bareSendCnt);
        System.out.println("-------------------------------");
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
