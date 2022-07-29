/*
 * Connection
 * 
 * Created 06/27/2006
 */
package com.topcoder.farm.shared.net.connection.api;

import java.io.IOException;

/**
 * Basic abstraction of a connection.
 * 
 * It contains the minimal features that a connection object should provide and
 * a callback mechanism to handle ordinary connection events.
 * 
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public interface Connection {
    /**
     * Sends the object through this connection.
     *  
     * @param object Object to be sent through this connection
     * @throws IOException if the object cannot be sent.
     */
    void send(Object object) throws IOException;
    
    /**
     * Closes this connection.
     *  
     * After invoking this method, the output channel of the
     * connection will be closed, future invocation to send method
     * will throw NotConnectionException. Nerverless, the connection could
     * still report received objects to the handler.
     * 
     * This method must not throw exceptions. 
     */
    void close();
    
    /**
     * Returns true if the connection is closed.
     * 
     * @return true if the connection is closed
     */
    boolean isClosed();

    
    /**
     * Returns true is the connection has been lost. When a connection is Lost it is also
     * closed. The closing of the connection due to an external event (e.g. connection closed by peer),
     * and not due to the close() invocation.
     * 
     * @return true if the connection has been lost
     */
    boolean isLost();
    
    /**
     * Sets the connection handler responsible for handling events of this connection.
     * 
     * @param handler Handler to be set, the handler must not be null
     */
    void setHandler(ConnectionHandler handler);
    
    /**
     * Returns the current connection Handler
     * 
     * @return the handler
     */
    ConnectionHandler getHandler();
    
    /**
     * Removes the current handler for this connection. 
     */
    void clearHandler();

    /**
     * Sets an attribute for this Connection
     * 
     * @param key Key of the attribute
     * @param value Value to set for the given key
     * @return The old value for the attribute, <code>null</code> if none was set
     */
    public Object setAttribute(String key, Object value);
    
    /**
     * Retrieves the attribute value for the given key
     *  
     * @param key Key of the attribute
     * @return The value for the given key, null if none was set 
     */
    public Object getAttribute(String key);
    
    
    /**
     * Retrieves and then removes the attribute value for the given key
     *  
     * @param key Key of the attribute
     * @return The value for the given key, null if none was set 
     */
    public Object removeAttribute(String key);

}
