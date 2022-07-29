/*
 * RequestResponseProcessorCnnFailTest
 * 
 * Created 08/16/2006
 */
package com.topcoder.farm.shared.net.connection.remoting;

import com.topcoder.farm.shared.net.connection.mock.MockConnection;
import com.topcoder.farm.shared.net.connection.remoting.RequestResponseProcessor.RequestProcessor;
import com.topcoder.farm.shared.util.concurrent.runner.DirectRunner;
import com.topcoder.farm.test.util.MTTestCase;

/**
 * Test case for RequestResponseProcessor class.
 * This test case is for checking that if required message could not be 
 * sent due to connection failure, no other message is sent
 * 
 * @author Diego Belfer (mural)
 * @version $$
 */
public class RequestResponseProcessorCnnFailTest extends MTTestCase {
    private MockConnection connection;
    private RequestResponseProcessor processor;
    
    protected void setUp() throws Exception {
        super.setUp();
        
        processor = new RequestResponseProcessor(new RequestProcessor() {
            public Object processRequest(Object requestObject) throws Exception {
                if ("THROW".equals(requestObject)) {
                    sleep(50);
                    throw new IllegalStateException("Thrown");
                } else  if ("WAIT".equals(requestObject)) { 
                    if (sleep(200)) {
                        throw new InterruptedException();
                    }
                }
                return requestObject.toString()+requestObject;
            }
        }, new DirectRunner());
        connection = new MockConnection();
        connection.setExceptionOnSend(true);
    }

    public void testProcessSyncSuccess() throws Exception{
        run(new Runnable() {
            public void run() {
                processor.processMessage(connection, new InvocationRequestMessage(1, "AA", InvocationRequestMessage.TYPE_SYNC));
            }
        });
        assertAll(1);
    }

    public void testProcessACKSuccess() throws Exception{
        run(new Runnable() {
            public void run() {
                processor.processMessage(connection, new InvocationRequestMessage(1, "AA", InvocationRequestMessage.TYPE_ACK));
            }
        });
        assertAll(1);
    }
    
    public void testProcessASyncSuccess() throws Exception{
        run(new Runnable() {
            public void run() {
                processor.processMessage(connection, new InvocationRequestMessage(1, "AA", InvocationRequestMessage.TYPE_ASYNC));
            }
        });
        assertAll(0);
    }

    public void testProcessSyncWithException() throws Exception{
        run(new Runnable() {
            public void run() {
                processor.processMessage(connection, new InvocationRequestMessage(1, "THROW", InvocationRequestMessage.TYPE_SYNC));
            }
        });
        assertAll(1);
    }

    public void testProcessAckWithException() throws Exception{
        run(new Runnable() {
            public void run() {
                processor.processMessage(connection, new InvocationRequestMessage(1, "THROW", InvocationRequestMessage.TYPE_ACK));
            }
        });
        assertAll(1);
    }
    
    public void testProcessAsyncWithException() throws Exception{
        run(new Runnable() {
            public void run() {
                processor.processMessage(connection, new InvocationRequestMessage(1, "THROW", InvocationRequestMessage.TYPE_ASYNC));
            }
        });
        assertAll(0);
    }
    
    private void assertAll(int size) throws InterruptedException {
        startAll();
        sleep(150);
        assertEquals(size, connection.getSendCount());
        assertTrue(waitAll(1));
    }
}
