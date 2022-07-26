/*
 * ConnectionListenerHandler
 * 
 * Created 07/04/2006
 */
package com.topcoder.farm.server.net.connection;

import com.topcoder.farm.shared.net.connection.api.Connection;

/**
 * ConnectionListenerHandler interface used by the ConnectionListener
 * to report new connections detected.
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public interface ConnectionListenerHandler {
    
    /**
     * This method is call every time a new connection
     * is detected by the ConnectionListener in which this handler
     * have been set.
     * 
     * @param connection The new connection detected
     */
    public void newConnection(Connection connection);
}