/*
 * ConnectionState
 * 
 * Created 07/13/2006
 */
package com.topcoder.farm.shared.net.connection.impl;

import java.io.IOException;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public interface ConnectionState {

    /**
     * Called when the send method is invoked on the connection
     * 
     * @param connection The connection on which the close method was invoked
     * @param object The object passed as argument to the send method of the connection 
     */
    public void send(AbstractConnection connection, Object object)
            throws NotConnectedException, IOException;

    /**
     * Called when the close method is invoked on the connection
     * 
     * The lock stateLock of the connection is already taken by the
     * current thread  
     *   
     * @param connection The connection on which the close method was invoked 
     */
    public void close(AbstractConnection connection);

    /**
     * Called when the handleConnectionLost method is invoked on the connection
     * 
     * The lock stateLock of the connection is already taken by the
     * current thread  
     *   
     * @param connection The connection on which the handleConnectionLost method was invoked 
     */
    public void handleConnectionLost(AbstractConnection connection);

    /**
     * Called when the handleConnectionClosed method is invoked on the connection
     * 
     * The lock stateLock of the connection is already taken by the
     * current thread  
     *   
     * @param connection The connection on which the handleConnectionClosed method was invoked 
     */
    public void handleConnectionClosed(AbstractConnection connection);

    /**
     * Called when the handleReceived method is invoked on the connection
     *   
     * @param connection The connection on which the handleReceived method was invoked
     * @param object The Object received  
     */
    public void handleReceived(AbstractConnection connection, Object object);

    /**
     * @return true if in this state the connection is closed
     */
    public boolean isClosed();

    /**
     * @return true if in this state the connection has been closed
     */
    public boolean isLost();

}