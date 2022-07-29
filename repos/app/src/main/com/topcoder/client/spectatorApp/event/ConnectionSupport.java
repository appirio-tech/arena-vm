/**
 * ConnectionSupport.java
 *
 * Description:		Event set support class for ConnectionListener.
 Manages listener registration and contains fire functions.
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.event;


import java.util.ArrayList;

/**
 * ConnectionSupport bottlenecks support for classes that fire events to
 * ConnectionListener listeners.
 */

public class ConnectionSupport {

    /** Holder for all listeners */
    private ArrayList connectionListeners = new ArrayList();

    /**
     * Adds a listener
     *
     * @param listener the listener to be added
     */
    public synchronized void addConnectionListener(ConnectionListener listener) {
        // add a listener if it is not already registered
        if (!connectionListeners.contains(listener)) {
            connectionListeners.add(listener);
        }
    }

    /**
     * Removes a listener
     *
     * @param listener the listener to be removed
     */
    public synchronized void removeConnectionListener(ConnectionListener listener) {
        // remove it if it is registered
        int pos = connectionListeners.indexOf(listener);
        if (pos >= 0) {
            connectionListeners.remove(pos);
        }
    }

    /**
     *  Notifies all listeners that a connection was lost
     *
     *  @param event   the event to send to the listener
     */
    public synchronized void fireConnectionLost(ConnectionEvent event) {
        // Fire the event to all listeners (done in reverse order from how they were added).
        for (int i = connectionListeners.size() - 1; i >= 0; i--) {
            ConnectionListener listener = (ConnectionListener) connectionListeners.get(i);
            listener.connectionLost(event);
        }
    }

    /**
     *  Notifies all listeners that a connection was made
     *
     *  @param event   the event to send to the listener
     */
    public synchronized void fireConnectionMade(ConnectionEvent event) {
        // Fire the event to all listeners (done in reverse order from how they were added).
        for (int i = connectionListeners.size() - 1; i >= 0; i--) {
            ConnectionListener listener = (ConnectionListener) connectionListeners.get(i);
            listener.connectionMade(event);
        }
    }


}

/* @(#)ConnectionSupport.java */
