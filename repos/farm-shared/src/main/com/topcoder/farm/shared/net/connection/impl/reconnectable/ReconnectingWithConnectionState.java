/*
 * ReconnectingWithConnectionState
 * 
 * Created 07/212006
 */
package com.topcoder.farm.shared.net.connection.impl.reconnectable;

import java.io.IOException;

import com.topcoder.farm.shared.net.connection.impl.AbstractConnection;
import com.topcoder.farm.shared.net.connection.impl.ConnectedState;
import com.topcoder.farm.shared.net.connection.impl.ConnectionState;

/**
 * State representing a reconnectable connection that is attempting to reconnect, 
 * already has the connection but it is uninitialized yet.
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ReconnectingWithConnectionState extends ConnectedState  implements ReconnectableConnectionState {
    /**
     * Singleton instance of this State
     */
    private static final ConnectionState instance = new ReconnectingWithConnectionState();
    
    /**
     * Returns an instance of this state
     * 
     * @return the Instance
     */
    public static ConnectionState getInstance() {
        return instance;
    }
    
    protected ReconnectingWithConnectionState() {
    }
    
    public void handleConnectionLost(AbstractConnection connection) {
        ((AutoReconnectConnection) connection).stateChangingFromConnectedToReconnecting();
    }
    
    public void handleConnectionClosed(AbstractConnection connection) {
        ((AutoReconnectConnection) connection).stateChangingFromReconnectingToClosing();
    }
    
    public void send(AbstractConnection connection, Object object) throws IOException {
        ((AutoReconnectConnection) connection).sendOnReconnect(object);
    }

    public void handleReconnectFail(AutoReconnectConnection connection) {
        connection.stateChangingFromReconnectingToLost();
    }

    public void handleReconnectSucceed(AutoReconnectConnection connection) {
        connection.stateChangingFromReconnectingToConnected();
    }
}
