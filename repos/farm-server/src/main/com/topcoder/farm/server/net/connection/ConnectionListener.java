/*
 * ConnectionListener
 * 
 * Created 06/27/2006
 */
package com.topcoder.farm.server.net.connection;

import java.io.IOException;



/**
 * Basic interface of a ConnectionListener
 *
 * A ConnectionListener when started, listens for new connections
 * and reports them to the ConnectionListenerHandler set for handling
 * this kind of events.
 * 
 * When the ConnectionListener is stopped, all connections reported by this
 * ConnectionListener will be lost and no more connections will be reported 
 * to the handler.
 *   
 *   
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public interface ConnectionListener {
    /**
     * Sets the handler that will handle new incoming connections
     * 
     * @param handler The handler to set
     */
    void setHandler(ConnectionListenerHandler handler);
    
    /**
     * Starts listening for new connections
     * The handler must be set prior to invoking this method
     * 
     * @throws IOException If the listener could not be started due to an IOException
     */
    void start() throws IOException;
    
    /**
     * Stops listening for new connections, and 
     * closes all connections reported by this 
     * ConnectionListener.
     */
    void stop();
}
