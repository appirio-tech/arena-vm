/*
 * ReconnectableConnectionHandler
 * 
 * Created 07/05/2006
 */
package com.topcoder.farm.shared.net.connection.api;

/**
 * Event Handler for a reconnectable connection. 
 * Extends the ConnectionHandler with methods to handle
 * events which are specific to ReconnectableConnections 
 *  
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public interface ReconnectableConnectionHandler extends ConnectionHandler {
    
    
    /**
     * This method is invoked when the connection has been temporally lost and
     * a reconnection attempt has started. 
     *  
     * @param connection Connection that is reconnecting
     */
    void reconnecting(ReconnectableConnection connection );

    
    /**
     * This method is invoked after a reconnection attempt has succeeded.
     * If the reconnect attempt failed, the connection will be reported as lost.
     * Since a connection could be used in multithread enviroments, and that this
     * method is called after the reconnection has succeed, no necessarilly 
     * 
     * @param connection The reconnected Connection. 
     */
    void reconnected(ReconnectableConnection connection);
    
    /**
     * This methiod is invoked after the connection has been restablished and prior to
     * <code>reconnected</code> notification.<p>
     * 
     * It is provided because the restablished connection could need some initialization. All re-initialization 
     * operations on the connection should be done from the calling thread. As an alternative, 
     * a specific message can be provided as the result of this method that will be send t.
     * 
     * @param connection The reconnected Connection that should be reinitialized.
     * @throws Exception If the reconnection could not be done. Throwing an exception will produce a new reconnection 
     *                   attempt. 
     * 
     */
    void performReconnection(ReconnectableConnection connection) throws Exception;
    
}
