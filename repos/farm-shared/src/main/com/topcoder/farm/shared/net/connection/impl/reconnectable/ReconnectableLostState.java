/*
 * ReconnectableLostState
 * 
 * Created 07/11/2006
 */
package com.topcoder.farm.shared.net.connection.impl.reconnectable;

import com.topcoder.farm.shared.net.connection.impl.ConnectionState;
import com.topcoder.farm.shared.net.connection.impl.LostState;

/**
 * State representing a reconnectable connection lost (closed) due to an external
 * event and not due to a close invocation on the connection.
 * When a reconnectable connection has this state, all reconnection attemps have failed 
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ReconnectableLostState extends LostState implements ReconnectableConnectionState {
    /**
     * Singleton instance of this State
     */
    private static final ConnectionState instance = new ReconnectableLostState();
    
    /**
     * Returns an instance of this state
     * 
     * @return the Instance
     */
    public static ConnectionState getInstance() {
        return instance;
    }
    
    protected ReconnectableLostState() {
    }

    public void handleReconnectFail(AutoReconnectConnection connection) {
    }

    public void handleReconnectSucceed(AutoReconnectConnection connection) {
    }
}
