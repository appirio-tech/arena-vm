/*
 * ReconnectableConnection
 * 
 * Created 06/27/2006
 */
package com.topcoder.farm.shared.net.connection.api;


/**
 * This interface provides additional functionality required by 
 * a reconnectable connection. A reconnectable connection is a connection 
 * that will try to reconnect to the endpoint at any time the connection
 * is lost. 
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public interface ReconnectableConnection extends Connection {
    
    /**
     * Sets the handler for this Connection
     * 
     * @param handler the handler to set
     */
    void setHandler(ReconnectableConnectionHandler handler);
    
    
    void forceReconnect();
}
