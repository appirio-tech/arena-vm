/*
 * ClosedState
 * 
 * Created 07/10/2006
 */
package com.topcoder.farm.shared.net.connection.impl;

/**
 * State representing a connection closed due to the invocation
 * of the close method
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ClosedState extends AbstractConnectionState {
    /**
     * Singleton instance of this State
     */
    private static final ConnectionState instance = new ClosedState();
    
    /**
     * Returns an instance of this state
     * 
     * @return the Instance
     */
    public static ConnectionState getInstance() {
        return instance;
    }

    protected ClosedState() {
    }

    public boolean isClosed() {
        return true;
    }

    public boolean isLost() {
        return false;
    }
}
