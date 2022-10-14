/*
 * ConnectedState
 * 
 * Created 07/10/2006
 */
package com.topcoder.farm.shared.net.connection.impl;

import java.io.IOException;


/**
 * State representing a connection working normally.
 *  
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ConnectedState extends AbstractConnectionState  {
    /**
     * Singleton instance of this State
     */
    private static final ConnectionState instance = new ConnectedState();
    
    /**
     * Returns an instance of this state
     * 
     * @return the Instance
     */
    public static ConnectionState getInstance() {
        return instance;
    }
    
    
    protected ConnectedState() {
    }
    
    public void send(AbstractConnection connection, Object object) throws IOException {
        connection.bareSend(object);
    }
    
    public void close(AbstractConnection connection) {
        connection.stateChangingFromConnectedToClosing();
    }

    public void handleConnectionLost(AbstractConnection connection) {
        connection.stateChangingFromConnectedToLost();
    }
    
    public void handleConnectionClosed(AbstractConnection connection) {
        connection.stateChangingFromConnectedToClosed();
    }
    
    public void handleReceived(AbstractConnection connection, Object object) {
        connection.doHandleReceived(object);
    }
    
    public boolean isClosed() {
        return false;
    }
    
    public boolean isLost() {
        return false;
    }
}
