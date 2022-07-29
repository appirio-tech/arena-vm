/**
 * ConnectionAdapter.java
 *
 * Description:		Adapter class that implements the listener with "do nothing" functionality
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.event;

public class ConnectionAdapter implements ConnectionListener {

    public void connectionMade(ConnectionEvent evt) {
    }

    public void connectionLost(ConnectionEvent evt) {
    }

}


/* @(#)ConnectionAdapter.java */
