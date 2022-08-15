/**
 * ConnectionListener.java
 *
 * Description:		Interface for connection events
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.event;

public interface ConnectionListener extends java.util.EventListener {

    /**
     * Method called whenever a connection was made
     *
     * @param evt the connection event
     */
    public void connectionMade(ConnectionEvent evt);

    /**
     * Method called whenever a connection was lost
     *
     * @param evt the connection event
     */
    public void connectionLost(ConnectionEvent evt);

}


/* @(#)ConnectionListener.java */
