/*
 * ReconnectableClosedState
 * 
 * Created 07/11/2006
 */
package com.topcoder.farm.shared.net.connection.impl.reconnectable;

import com.topcoder.farm.shared.net.connection.impl.ClosedState;
import com.topcoder.farm.shared.net.connection.impl.ConnectionState;

/**
 * State representing a reconnectable connection closed due to the invocation
 * of the close method
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ReconnectableClosedState extends ClosedState implements ReconnectableConnectionState {
    /**
     * Singleton instance of this State
     */
    private static final ConnectionState instance = new ReconnectableClosedState();
    
    /**
     * Returns an instance of this state
     * 
     * @return the Instance
     */
    public static ConnectionState getInstance() {
        return instance;
    }
    
    protected ReconnectableClosedState() {
    }

    public void handleReconnectFail(AutoReconnectConnection connection) {
    }

    public void handleReconnectSucceed(AutoReconnectConnection connection) {
    }
}
