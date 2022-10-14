/*
 * MockConnection
 * 
 * Created 07/14/2006
 */
package com.topcoder.farm.shared.net.connection.mock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.topcoder.farm.shared.net.connection.api.Connection;
import com.topcoder.farm.shared.net.connection.api.ConnectionHandler;
import com.topcoder.farm.shared.net.connection.impl.NotConnectedException;
import com.topcoder.farm.shared.net.connection.impl.NullConnectionHandler;
import com.topcoder.farm.shared.util.queue.Queue;
import com.topcoder.farm.shared.util.queue.QueueImpl;

/**
 * Mock object for Connection
 * 
 * This class is a mock for the Connection class,
 * provides ways for simulating common connection events
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class MockConnection implements Connection {
    private ConnectionEventProcessor eventProcessor;
    private Queue events = new QueueImpl(); 
    
    
    /**
     * When set, send method will throw an exception
     */
    private boolean exceptionOnSend = false;
    
    /**
     * When set, in the moment the send method is invoke, the connection is 
     * notified as lost. It uses the calling thread to notify the lost  
     */
    private boolean lostOnSend = false;
    
    /**
     * The connection handler
     */
    private ConnectionHandler handler;

    /**
     * Flags indicating the state of the connection
     */
    private boolean lost = false;
    private boolean closed = false;
    private boolean closing = false;
    
    /**
     * List of object used as arguments in the send method
     */
    private List sentObjects = new ArrayList();
    private int sendCount;

    
    public MockConnection() {
        eventProcessor = new ConnectionEventProcessor(events, buildHandler());
        eventProcessor.start();
    }
    
    private ConnectionHandler buildHandler() {
        return new ConnectionHandler() {
            public void receive(Connection connection, Object message) {
                getHandler().receive(connection, message);
            }
        
            public void connectionLost(Connection connection) {
                getHandler().connectionLost(connection);
                eventProcessor.stop();
                
            }
        
            public void connectionClosed(Connection connection) {
                getHandler().connectionClosed(connection);
                eventProcessor.stop();
            }
        };
    }

    /**
     * @see Connection#clearHandler()
     */
    public synchronized void clearHandler() {
        handler = NullConnectionHandler.INSTANCE;
    }

    /**
     * @see Connection#close()
     * Sets the closing flag.
     */
    public synchronized void close() {
        this.closing  = true;
    }

    /**
     * @see Connection#getHandler()
     */
    public synchronized ConnectionHandler getHandler() {
        return handler;
    }

    /**
     * @see Connection#isClosed()
     * @return true if lost, closed either closing flag is set.
     */
    public synchronized boolean isClosed() {
        return lost || closed || closing; 
    }

    /**
     * @see Connection#isLost()
     * @return true if lost flag is set
     */    
    public synchronized boolean isLost() {
        return lost;
    }

    /**
     * @see Connection#send(Object)
     *
     * Note:
     * If exceptionOnSend is set, this method throws IOException.
     * If lostOnSend is set, this method invokes forceLost to generate a connection lost 
     * report.
     * In other case <code>object</code> is added to the <code>sentObjects</code> list
     */
    public synchronized void send(Object object) throws IOException {
        sendCount++;
        if (isClosed()) throw new NotConnectedException();
        if (exceptionOnSend) throw new IOException();
        if (!lostOnSend) {
            sentObjects.add(object);
        } else {
            forceLost();
        }
    }

    /**
     * @see Connection#setHandler(ConnectionHandler)
     */
    public synchronized void setHandler(ConnectionHandler handler) {
        this.handler = handler;
    }
    
    /**
     * Forces a connection lost notification. 
     * Sets the lost flag.
     */
    public void forceLost() {
        synchronized (this) {
            lost = true;
        }
        events.offer(ConnectionEvent.lostEvent(this));
    }
    
    /**
     * Force a connection closed notification.
     * Sets the closed flag.
     *
     */
    public void forceClosed() {
        synchronized (this) {
            closed = true;
        }
        events.offer(ConnectionEvent.closedEvent(this));
    }
    
    /**
     * Forces a received notification
     * @param obj obj to notify as received
     */
    public void forceReceived(Object obj) {
        events.offer(ConnectionEvent.receivedEvent(this, obj));
    }
    
    /**
     * @return The closing flag value.
     */
    public boolean isClosingFlag() {
        return closing;
    }
    
    /**
     * @return The lost flag value
     */
    public boolean isLostFlag() {
        return lost;
    }
    
    /**
     * @return The closed flag value
     */
    public boolean isClosedFlag() {
        return closed;
    }
    
    /**
     * @return a copy of the <code>sentObjects</code> List
     */
    public synchronized List getSentObjects() {
        return new ArrayList(sentObjects);
    }

    public synchronized boolean isExceptionOnSend() {
        return exceptionOnSend;
    }

    public synchronized void setExceptionOnSend(boolean exceptionOnSend) {
        this.exceptionOnSend = exceptionOnSend;
    }

    public synchronized boolean isLostOnSend() {
        return lostOnSend;
    }

    public synchronized void setLostOnSend(boolean lostOnSend) {
        this.lostOnSend = lostOnSend;
    }

    public Object getAttribute(String key) {
        return null;
    }

    public Object removeAttribute(String key) {
        return null;
    }

    public Object setAttribute(String key, Object value) {
        return null;
    }
    
    public int getSendCount() {
        return sendCount;
    }
}
