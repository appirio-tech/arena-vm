/*
 * RequestResponseHandler
 * 
 * Created 07/18/2006
 */
package com.topcoder.farm.shared.net.connection.remoting;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.topcoder.client.netClient.ResponseToSyncRequestWaiter;
import com.topcoder.client.netClient.ResponseWaiterManager;
import com.topcoder.farm.shared.net.connection.api.Connection;

/**
 * This class is the emiter side of the classes involved in request-response 
 * simulation. 
 * 
 * Synchronous request can be made through a connection using this 
 * 
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class RequestResponseHandler implements Invoker, MessageProcessor {
    /**
     * Manager responsible to handling registered waiters 
     */
    private ResponseWaiterManager waiters;
    
    /**
     * Map containing responses of invocation (inter-thread communication) 
     */
    private Map responses = new ConcurrentHashMap();
    
    /**
     * This field contains the id for the lastest generated request 
     */
    private int invocationId = 0;

    /**
     * Max time to wait for an ack response
     */
    private long ackTimeout;

    /**
     * Max time to wait for a sync response
     */
    private long syncTimeout; 
    
    
    /**
     * Creates a new RequestResponseHandler.
     */
    public RequestResponseHandler() {
        this(5000);
    }

    /**
     * Creates a new RequestResponseHandler using the specific timeout 
     * for response waiting.
     * 
     * @param ackTimeout Time in milliseconds to wait for an ACK response
     */
    public RequestResponseHandler(int ackTimeout) {
        this.ackTimeout = ackTimeout;
        this.syncTimeout = ((long)ackTimeout*2);
        this.waiters = new ResponseWaiterManager(ackTimeout, ackTimeout, Long.MAX_VALUE);
    }

    /**
     * Send InvocationRequestMessage (SYNC) through the connection and wait until a InvocationResponseMessage
     * be received for that message. 
     * 
     * A SYNC message must be processed by the peer and just then a response 
     * should be delivered. 
     * 
     * @param request The request object to send
     * @return The value received in the InvocationResponseMessage 
     * 
     * @throws TimeoutException If the time defined to wait for a response has expired
     * @throws RemotingException If the request could not be delivered
     * @throws Exception If an exception was received as response 
     */
    public Object invokeSync(Connection connection, Object request) throws TimeoutException, RemotingException, Exception {
        return invokeAndWait(connection, request, InvocationRequestMessage.TYPE_SYNC);
    }

    /**
     * Send InvocationRequestMessage (ACK) through the connection and wait until a InvocationResponseMessage
     * be received for that message. 
     * 
     * Peer must deliver InvocationResponseMessage as soon it start processing the Request.
     * 
     * @param request The request object to send
     * 
     * @throws TimeoutException If the time defined to wait for a response has expired
     * @throws RemotingException If the request could not be delivered
     */
    public void invoke(Connection connection, Object request) throws TimeoutException, RemotingException, InterruptedException {
        int syncType = InvocationRequestMessage.TYPE_ACK;
        try {
            invokeAndWait(connection, request, syncType);
        } catch (RuntimeException e) {
            throw e;
        } catch (InterruptedException e) {
            throw e;
        } catch (Exception e) {
            //Doesn't happen
        }
    }
    
    
    /**
     * Sends InvocationRequestMessage (ASYNC) through the connection
     * 
     * Peer should not deliver InvocationResponseMessage for this message
     * 
     * @param o The request object to send
     * @throws RemotingException If the request could not be delivered
     */
    public void invokeAsync(Connection connection, Object o) throws RemotingException {
        try {
            int id = nextId();
            connection.send(new InvocationRequestMessage(id, o, InvocationRequestMessage.TYPE_ASYNC));
        } catch (IOException e) {
            throw new RemotingException(e);
        }
    }
    
    /**
     * Processes response messages
     */
    public boolean processMessage(Connection connection, Object o) {
        if (o instanceof InvocationResponseMessage) {
            Integer id = new Integer(((InvocationResponseMessage) o).getId());
            responses.put(id, o);
            if (!waiters.endOfSyncResponse(id.intValue())) {
                getAndRemoveResponse(id);
            }
            return true;
        }
        return false;
    }

    /**
     * @see MessageProcessor#connectionLost(Connection)
     * 
     * Impl: Forces timeout on all threads waiting for responses
     */
    public void connectionLost(Connection connection) {
        waiters.timeOutAll();
    }
    
    
    
    /**
     * Sends a blocking InvocationRequestMessage through the connection. 
     * The calling thread will be blocked until a response be received from the peer
     * or a time out occur.
     * 
     * 
     * @param connection The connection to use
     * @param request The request object to send
     * @param syncType The type of synchronous message to send (SYNC or ACK)
     * @throws RemotingException If the request could not be delivered
     * @throws InterruptedException If the thread waiting for a response was interrupted
     * @throws Exception if the peer send an exception as result 
     */
    private Object invokeAndWait(Connection connection, Object request, int syncType) throws InterruptedException, RemotingException, Exception {
        boolean timeout = false;
        int id = nextId();
        ResponseToSyncRequestWaiter waiter = null; 
        if (syncType == InvocationRequestMessage.TYPE_ACK && ackTimeout != Long.MAX_VALUE) {
            waiter = waiters.registerWaiterFor(id);
        } else {
            waiter = waiters.registerWaiterFor(id, syncTimeout, syncTimeout, Long.MAX_VALUE);
        }
        try {
            connection.send(new InvocationRequestMessage(id, request, syncType));
            if (waiter.blockUntilEnd()) {
                timeout = true;
                throw new TimeoutException();
            }
            InvocationResponseMessage response = getAndRemoveResponse(new Integer(id));
            if (response instanceof ExceptionInvocationResponse) {
                ExceptionInvocationResponse exResponse = (ExceptionInvocationResponse) response;
                throw exResponse.getException();
            } else {
                return response.getResponseObject();
            }
        } catch (IOException e) {
            throw new RemotingException(e);
        } finally {
            waiters.unregisterWaiterFor(id);
            if (timeout) {
                getAndRemoveResponse(new Integer(id));
            }
        }
    }
    
    /**
     * Waits for all pending responses and acks for <code>timeout </code>milliseconds
     * 
     * @param timeout The time in ms to wait for responses and acks
     */
    public void waitForAllInvocations(long timeout) {
        long maxTime;
        if (timeout == 0) {
            maxTime = Long.MAX_VALUE;
        } else {
            maxTime = System.currentTimeMillis() + timeout;
        }
        while (waiters.size() < 0 && System.currentTimeMillis() < maxTime) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
    }
    /**
     * Removes and retrieves the InvocationResponseMessage received for the given id
     * 
     * @param id The id of the requests
     * @return the InvocationResponseMessage or null if none was received
     */
    private InvocationResponseMessage getAndRemoveResponse(Integer id) {
        return (InvocationResponseMessage) responses.remove(id);
    }
    
    /**
     * Local id generation method used to identify requests and responses
     */
    private synchronized int nextId() {
        return ++invocationId;
    }
}
