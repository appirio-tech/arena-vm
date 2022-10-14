/*
 * LostState
 * 
 * Created 07/10/2006
 */
package com.topcoder.farm.shared.net.connection.impl;

/**
 * State representing a connection lost (closed) due to an external
 * event and not due to a close invocation on the connection
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class LostState extends AbstractConnectionState {
    /**
     * Singleton instance of this State
     */
    private static final ConnectionState instance = new LostState();
    
    /**
     * Returns an instance of this state
     * 
     * @return the Instance
     */
    public static ConnectionState getInstance() {
        return instance;
    }
    
    protected LostState() {
    }
    
    public boolean isClosed() {
        return true;
    }

    public boolean isLost() {
        return true;
    }
}
