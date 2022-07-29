/*
 * ReconnectableClosingState
 * 
 * Created 07/11/2006
 */
package com.topcoder.farm.shared.net.connection.impl.reconnectable;

import com.topcoder.farm.shared.net.connection.impl.ClosingState;
import com.topcoder.farm.shared.net.connection.impl.ConnectionState;

/**
 * State representing a reconnectable connection on which the close method was invoked but
 * the notification of the closing of the connection has not been received
 *  
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ReconnectableClosingState extends ClosingState implements ReconnectableConnectionState {
    /**
     * Singleton instance of this State
     */
    private static final ConnectionState instance = new ReconnectableClosingState();
    
    /**
     * Returns an instance of this state
     * 
     * @return the Instance
     */
    public static ConnectionState getInstance() {
        return instance;
    }
    
    protected ReconnectableClosingState() {
    }

    public void handleReconnectFail(AutoReconnectConnection connection) {
        connection.stateChangingFromClosingToClosed();
    }

    public void handleReconnectSucceed(AutoReconnectConnection connection) {
        connection.stateChangingFromConnectedToClosing();
    }
}
