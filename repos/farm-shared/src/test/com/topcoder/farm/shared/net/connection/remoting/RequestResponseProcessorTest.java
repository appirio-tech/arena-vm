/*
 * RequestResponseProcessorTest
 * 
 * Created 08/16/2006
 */
package com.topcoder.farm.shared.net.connection.remoting;

import com.topcoder.farm.controller.exception.InternalControllerException;
import com.topcoder.farm.shared.net.connection.mock.MockConnection;
import com.topcoder.farm.shared.net.connection.remoting.RequestResponseProcessor.RequestProcessor;
import com.topcoder.farm.shared.util.concurrent.runner.DirectRunner;
import com.topcoder.farm.test.util.MTTestCase;

/**
 * Test case for RequestResponseProcessor class
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class RequestResponseProcessorTest extends MTTestCase {
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
    }

    /**
     * Tests that when an Sync InvocationRequestMessage is received and it is processed successfully
     * the result is sent through the connection using an {@link InvocationResponseMessage} with the same id.
     */
    public void testProcessSyncSuccess() throws Exception{
        run(new Runnable() {
            public void run() {
                processor.processMessage(connection, new InvocationRequestMessage(1, "WAIT", InvocationRequestMessage.TYPE_SYNC));
            }
        });
        startAll();
        sleep(100);
        assertEquals(0, connection.getSentObjects().size());
        assertTrue(waitAll(200));
        assertEquals(1, connection.getSentObjects().size());
        assertEquals(1, getItemSent(connection, 0).getId());
        assertEquals(InvocationResponseMessage.class, getItemSent(connection, 0).getClass());
        assertEquals(getItemSent(connection, 0).getResponseObject(), "WAITWAIT");
    }

    
    /**
     * Tests that when an Ack InvocationRequestMessage is received, an {@link InvocationResponseMessage} 
     * with the same id is send through the connection immediately and no other message is sent even if 
     * the processing produced a result value
     */
    public void testProcessACKSuccess() throws Exception{
        run(new Runnable() {
            public void run() {
                processor.processMessage(connection, new InvocationRequestMessage(1, "WAIT", InvocationRequestMessage.TYPE_ACK));
            }
        });
        startAll();
        sleep(100);

        assertEquals(1, connection.getSentObjects().size());
        assertEquals(1, getItemSent(connection, 0).getId());
        assertEquals(InvocationResponseMessage.class, getItemSent(connection, 0).getClass());

        assertTrue(waitAll(150));
        assertEquals(1, connection.getSentObjects().size());
    }
    
    /**
     * Tests that when an Async InvocationRequestMessage is received, no message is sent through the 
     * connection even if the processing produced a result value
     */
    public void testProcessASyncSuccess() throws Exception{
        run(new Runnable() {
            public void run() {
                processor.processMessage(connection, new InvocationRequestMessage(1, "AA", InvocationRequestMessage.TYPE_ASYNC));
            }
        });
        startAll();
        assertTrue(waitAll(100));
        assertEquals(0, connection.getSentObjects().size());
    }

    /**
     * Tests that when an Sync InvocationRequestMessage is received and while processing an exception
     * was thrown the exception is sent through the connection using an {@link ExceptionInvocationResponse} 
     * with the same id.
     */
    public void testProcessSyncWithException() throws Exception{
        run(new Runnable() {
            public void run() {
                processor.processMessage(connection, new InvocationRequestMessage(1, "THROW", InvocationRequestMessage.TYPE_SYNC));
            }
        });
        startAll();
        assertTrue(waitAll(100));
        assertEquals(1, connection.getSentObjects().size());
        assertEquals(1, getItemSent(connection, 0).getId());
        assertEquals(ExceptionInvocationResponse.class, getItemSent(connection, 0).getClass());
        Exception exception = ((ExceptionInvocationResponse) getItemSent(connection, 0)).getException();
        assertEquals(InternalControllerException.class, exception.getClass());
        assertTrue(exception.getMessage().indexOf(IllegalStateException.class.getName()) > -1);
    }
    

    /**
     * Tests that when an Sync InvocationRequestMessage is received an {@link InvocationResponseMessage} 
     * with the same id is send through the connection immediately and no other message is sent even if 
     * the processing threw an exception
     */
    public void testProcessAckWithException() throws Exception{
        run(new Runnable() {
            public void run() {
                processor.processMessage(connection, new InvocationRequestMessage(1, "THROW", InvocationRequestMessage.TYPE_ACK));
            }
        });
        startAll();
        assertTrue(waitAll(100));
        assertEquals(1, connection.getSentObjects().size());
        assertEquals(1, getItemSent(connection, 0).getId());
        assertEquals(InvocationResponseMessage.class, getItemSent(connection, 0).getClass());
    }
    
    
    /**
     * Tests that when an Async InvocationRequestMessage is received, no message is sent through the 
     * connection even if the processing threw an exception  
     */
    public void testProcessAsyncWithException() throws Exception{
        run(new Runnable() {
            public void run() {
                processor.processMessage(connection, new InvocationRequestMessage(1, "THROW", InvocationRequestMessage.TYPE_ASYNC));
            }
        });
        startAll();
        assertTrue(waitAll(100));
        assertEquals(0, connection.getSentObjects().size());
    }

    private InvocationResponseMessage getItemSent(final MockConnection connection, int pos) {
        return (InvocationResponseMessage) connection.getSentObjects().get(pos);
    }
}
