/*
 * ConnectedState
 * 
 * Created 07/10/2006
 */
package com.topcoder.farm.shared.net.connection.impl;



/**
 * State representing a connection on which the close method was invoked but
 * the notification of the closing of the connection has not been received
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ClosingState extends AbstractConnectionState  {
    /**
     * Singleton instance of this State
     */
    private static final ConnectionState instance = new ClosingState();
    
    /**
     * Returns an instance of this state
     * 
     * @return the Instance
     */
    public static ConnectionState getInstance() {
        return instance;
    }

    protected ClosingState() {
    }
    
    public void handleConnectionClosed(AbstractConnection connection) {
        connection.stateChangingFromClosingToClosed();
    }
    
    public void handleConnectionLost(AbstractConnection connection) {
        connection.stateChangingFromClosingToClosed();
    }
    
    public void handleReceived(AbstractConnection connection, Object object) {
        connection.doHandleReceived(object);
    }
    
    public boolean isClosed() {
        return true;
    }
    
    public boolean isLost() {
        return false;
    }
}
