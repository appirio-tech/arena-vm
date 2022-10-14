/*
 * ConnectionHandler
 * 
 * Created 07/04/2006
 */
package com.topcoder.farm.shared.net.connection.api;

/**
 * Callback interface to handle Connection events.
 *   
 * @author Diego Belfer (Mural)
 * @version $Id$
 */
public interface ConnectionHandler {
    
    /**
     * Invoked when an object has been received through the connection
     * 
     * @param connection Connection wich has received the object 
     * @param object Object received
     */
    void receive(Connection connection, Object object);
    
    /**
     * Invoked when the connection has been closed due to an invocation
     * to the close method of the connection
     * 
     * @param connection The closed connection
     */
    void connectionClosed(Connection connection);
    
    /**
     * Invoked when the connection has been lost. When this method is invoked
     * the connection is already closed.
     * 
     * @param connection The lost connection
     */
    void connectionLost(Connection connection);
}