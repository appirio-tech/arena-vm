/*
 * ReconnectableConnectedState
 * 
 * Created 07/11/2006
 */
package com.topcoder.farm.shared.net.connection.impl.reconnectable;

import com.topcoder.farm.shared.net.connection.impl.AbstractConnection;
import com.topcoder.farm.shared.net.connection.impl.ConnectedState;
import com.topcoder.farm.shared.net.connection.impl.ConnectionState;

/**
 * State representing a reconnectable connection working normally.
 *  
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ReconnectableConnectedState extends ConnectedState implements ReconnectableConnectionState {
    /**
     * Singleton instance of this State
     */
    private static final ConnectionState instance = new ReconnectableConnectedState();
    
    /**
     * Returns an instance of this state
     * 
     * @return the Instance
     */
    public static ConnectionState getInstance() {
        return instance;
    }
    
    protected ReconnectableConnectedState() {
    }
    
    public void handleConnectionLost(AbstractConnection connection) {
        ((AutoReconnectConnection) connection).stateChangingFromConnectedToReconnecting();
    }

    public void handleReconnectFail(AutoReconnectConnection connection) {
        connection.stateChangingFromConnectedToLost();
    }

    public void handleReconnectSucceed(AutoReconnectConnection connection) {
        throw new IllegalStateException("Invalid event for connectino state");
    }
}
