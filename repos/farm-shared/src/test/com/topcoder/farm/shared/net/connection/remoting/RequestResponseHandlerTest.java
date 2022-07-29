/*
 * RequestResponseHandlerTest
 * 
 * Created 08/16/2006
 */
package com.topcoder.farm.shared.net.connection.remoting;

import com.topcoder.farm.shared.net.connection.mock.MockConnection;
import com.topcoder.farm.test.util.MTTestCase;

/**
 * Test case for RequestResponseHandler class
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class RequestResponseHandlerTest extends MTTestCase {
    protected volatile Exception exception;
    protected volatile Object responseObject;
    protected volatile Exception exception2;
    
    protected void setUp() throws Exception {
        super.setUp();
        exception = null;
        responseObject = null;
        exception2 = null;
    }
    
    /**
     * Tests that an InvocationRequestMessage that not isSync 
     * and not requiresAck is sent through the connection 
     * when calling invokeAsync method
     */
    public void testInvokeASync() throws Exception{
        final MockConnection connection = new MockConnection();
        RequestResponseHandler handler = new RequestResponseHandler();
        handler.invokeAsync(connection, new Long(1));
        assertEquals(1, connection.getSentObjects().size());
        assertFalse(getItemSent(connection,0).isSync());
        assertFalse(getItemSent(connection,0).requiresAck());
    }

    
    /**
     * Tests that an InvocationRequestMessage that isSync is sent through the connection
     * when invokeSync is invoked
     */
    public void testInvokeSync() throws Exception{
        final MockConnection connection = new MockConnection();
        final  RequestResponseHandler handler = new RequestResponseHandler();
        run(new Runnable() {
            public void run() {
                try {
                    handler.invokeSync(connection, new Long(1));
                } catch (Exception e) {
                    exception = e;
                }
            }
        });
        startAll();
        waitAll(200);
        assertNotNull(exception);
        assertEquals(InterruptedException.class, exception.getClass());
        
        assertEquals(1, connection.getSentObjects().size());
        assertTrue(getItemSent(connection,0).isSync());
        assertFalse(getItemSent(connection,0).requiresAck());
    }
    
    
    /**
     * Tests that an InvocationRequestMessage that not isSync but requiresAck is sent 
     * through the connection when invoke is called
     */
    public void testInvoke() throws Exception{
        final MockConnection connection = new MockConnection();
        final  RequestResponseHandler handler = new RequestResponseHandler();
        run(new Runnable() {
            public void run() {
                try {
                    handler.invoke(connection, new Long(1));
                } catch (Exception e) {
                    exception = e;
                }
            }
        });
        startAll();
        waitAll(200);
        assertNotNull(exception);
        assertEquals(InterruptedException.class, exception.getClass());
        
        assertEquals(1, connection.getSentObjects().size());
        assertFalse(getItemSent(connection,0).isSync());
        assertTrue(getItemSent(connection,0).requiresAck());
    }
    
    
    /**
     * Tests that a time out occurs if no response message is given to process to the RequestHandler after
     * calling invokeSync method 
     */
    public void testInvokeSyncTimeOut() throws Exception{
        final MockConnection connection = new MockConnection();
        final  RequestResponseHandler handler = new RequestResponseHandler(150);
        run(new Runnable() {
            public void run() {
                try {
                    handler.invokeSync(connection, new Long(1));
                } catch (Exception e) {
                    exception = e;
                }
            }
        });
        startAll();
        waitAll(450);
        assertNotNull(exception);
        assertEquals(TimeoutException.class, exception.getClass());
    }

    /**
     * Tests that a time out occurs if no response message is given to process to the RequestHandler after
     * calling invoke method 
     */
    public void testInvokeTimeOut() throws Exception{
        final MockConnection connection = new MockConnection();
        final  RequestResponseHandler handler = new RequestResponseHandler(150);
        run(new Runnable() {
            public void run() {
                try {
                    handler.invoke(connection, new Long(1));
                } catch (Exception e) {
                    exception = e;
                }
            }
        });
        startAll();
        waitAll(250);
        assertNotNull(exception);
        assertEquals(TimeoutException.class, exception.getClass());
    }

    /**
     * Tests that a time out occurs even if a reponse message with a different id is given 
     * to process to the RequestHandler after calling invokeSync method 
     */
    public void testInvokeSyncTimeOutWithWrongResponse() throws Exception{
        final MockConnection connection = new MockConnection();
        final  RequestResponseHandler handler = new RequestResponseHandler(150);
        run(new Runnable() {
            public void run() {
                try {
                    handler.invokeSync(connection, new Long(1));
                } catch (Exception e) {
                    exception = e;
                }
            }
        });
        run(new Runnable() {
            public void run() {
                try {
                    sleep(50);
                    InvocationRequestMessage msg = (InvocationRequestMessage) connection.getSentObjects().get(0);
                    handler.processMessage(connection, new InvocationResponseMessage(msg.getId()+1, msg.getRequestObject()));
                } catch (Exception e) {
                    exception2 = e;
                }
            }
        });
        startAll();
        waitAll(450);
        assertNotNull(exception);
        assertEquals(TimeoutException.class, exception.getClass());
    }

    /**
     * Tests that a time out occurs even if a reponse message with a different id is given 
     * to process to the RequestHandler after calling invoke method 
     */
    public void testInvokeTimeOutWithWrongResponse() throws Exception{
        final MockConnection connection = new MockConnection();
        final  RequestResponseHandler handler = new RequestResponseHandler(150);
        run(new Runnable() {
            public void run() {
                try {
                    handler.invoke(connection, new Long(1));
                } catch (Exception e) {
                    exception = e;
                }
            }
        });
        run(new Runnable() {
            public void run() {
                try {
                    sleep(50);
                    InvocationRequestMessage msg = (InvocationRequestMessage) connection.getSentObjects().get(0);
                    handler.processMessage(connection, new InvocationResponseMessage(msg.getId()+1, msg.getRequestObject()));
                } catch (Exception e) {
                    exception2 = e;
                }
            }
        });
        startAll();
        waitAll(250);
        assertNotNull(exception);
        assertEquals(TimeoutException.class, exception.getClass());
    }
    
    /**
     * Tests that the response object contained in a matching {@link InvocationResponseMessage} 
     * given to process to the RequestHandler after calling invokeSync method is returned 
     * by invokeSync   
     */
    public void testResponseReceived() throws Exception{
        final MockConnection connection = new MockConnection();
        final RequestResponseHandler handler = new RequestResponseHandler();
        run(new Runnable() {
            public void run() {
                try {
                    responseObject = handler.invokeSync(connection, "A");
                } catch (Exception e) {
                    exception = e;
                }
            }
        });

        run(new Runnable() {
            public void run() {
                try {
                    sleep(50);
                    InvocationRequestMessage msg = (InvocationRequestMessage) connection.getSentObjects().get(0);
                    handler.processMessage(connection, new InvocationResponseMessage(msg.getId(), msg.getRequestObject()));
                } catch (Exception e) {
                    exception2 = e;
                }
            }
        });
        startAll();
        waitAll(200);
        assertNull(exception);
        assertNull(exception2);
        assertEquals("A", responseObject);
    }
    
    
    /**
     * Tests that if a matching {@link ExceptionInvocationResponse} is given to process to the 
     * RequestHandler after calling invokeSync method, the invokeSync throws the exception contained   
     */
    public void testExceptionResponseReceived() throws Exception{
        final MockConnection connection = new MockConnection();
        final RequestResponseHandler handler = new RequestResponseHandler();
        run(new Runnable() {
            public void run() {
                try {
                    responseObject = handler.invokeSync(connection, "A");
                } catch (Exception e) {
                    exception = e;
                }
            }
        });

        run(new Runnable() {
            public void run() {
                try {
                    sleep(50);
                    InvocationRequestMessage msg = (InvocationRequestMessage) connection.getSentObjects().get(0);
                    handler.processMessage(connection, new ExceptionInvocationResponse(msg.getId(), new IllegalStateException()));
                } catch (Exception e) {
                    exception2 = e;
                }
            }
        });
        startAll();
        waitAll(200);
        assertNotNull(exception);
        assertEquals(IllegalStateException.class, exception.getClass());
        assertNull(exception2);
    }
    
    
    /**
     * Tests that if a matching {@link InvocationResponseMessage} is given to process to the 
     * RequestHandler after calling invoke method, it unblocks the waiting thread   
     */
    public void testInvokeUnblock() throws Exception{
        final MockConnection connection = new MockConnection();
        final RequestResponseHandler handler = new RequestResponseHandler();
        run(new Runnable() {
            public void run() {
                try {
                    handler.invoke(connection, "A");
                } catch (Exception e) {
                    exception = e;
                }
            }
        });

        run(new Runnable() {
            public void run() {
                try {
                    sleep(50);
                    InvocationRequestMessage msg = (InvocationRequestMessage) connection.getSentObjects().get(0);
                    handler.processMessage(connection, new InvocationResponseMessage(msg.getId(), null));
                } catch (Exception e) {
                    exception2 = e;
                }
            }
        });
        startAll();
        waitAll(200);
        assertNull(exception);
        assertNull(exception2);
    }

    private InvocationRequestMessage getItemSent(final MockConnection connection, int pos) {
        return (InvocationRequestMessage) connection.getSentObjects().get(pos);
    }
    
}
