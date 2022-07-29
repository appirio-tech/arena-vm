/**
 * StatusChangeSupport.java
 *
 * Description:		Event set support class for StatusChangeListener.
 Manages listener registration and contains fire functions.
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.scoreboard.model;


import java.util.ArrayList;

/**
 * StatusChangeSupport bottlenecks support for classes that fire events to
 * StatusChangeListener listeners.
 */

public class StatusChangeSupport implements java.io.Serializable {

    /** Holder for all listeners */
    private ArrayList statusChangeListeners = new ArrayList();

    /**
     * Adds a listener
     *
     * @param listener the listener to be added
     */
    public synchronized void addStatusChangeListener(StatusChangeListener listener) {
        // add a listener if it is not already registered
        if (!statusChangeListeners.contains(listener)) {
            statusChangeListeners.add(listener);
        }
    }

    /**
     * Removes a listener
     *
     * @param listener the listener to be removed
     */
    public synchronized void removeStatusChangeListener(StatusChangeListener listener) {
        // remove it if it is registered
        int pos = statusChangeListeners.indexOf(listener);
        if (pos >= 0) {
            statusChangeListeners.remove(pos);
        }
    }

    /**
     * Fires notifications off to all listeners (in reverse order)
     *
     * @param evt the status change event
     */
    public synchronized void fireUpdateStatus(StatusChangeEvent evt) {
        // Fire the event to all listeners (done in reverse order from how they were added).
        for (int i = statusChangeListeners.size() - 1; i >= 0; i--) {
            StatusChangeListener listener = (StatusChangeListener) statusChangeListeners.get(i);
            listener.updateStatus(evt);
        }
    }


}

/* @(#)StatusChangeSupport.java */
