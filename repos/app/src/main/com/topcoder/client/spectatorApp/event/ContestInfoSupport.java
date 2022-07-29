/**
 * ContestInfoSupport.java
 *
 * Description:		Event set support class for ContestInfoListener.
 Manages listener registration and contains fire functions.
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.event;

import java.util.ArrayList;

/**
 * ContestInfoSupport bottlenecks support for classes that fire events to
 * ContestInfoListener listeners.
 */

public class ContestInfoSupport implements java.io.Serializable {

    /** Holder for all listeners */
    private ArrayList contestInfoListeners = new ArrayList();

    /**
     * Adds a listener
     *
     * @param listener the listener to be added
     */
    public synchronized void addContestInfoListener(ContestInfoListener listener) {
        // add a listener if it is not already registered
        if (!contestInfoListeners.contains(listener)) {
            contestInfoListeners.add(listener);
        }
    }

    /**
     * Removes a listener
     *
     * @param listener the listener to be removed
     */
    public synchronized void removeContestInfoListener(ContestInfoListener listener) {
        // remove it if it is registered
        int pos = contestInfoListeners.indexOf(listener);
        if (pos >= 0) {
            contestInfoListeners.remove(pos);
        }
    }

    /**
     *  Notifies all listeners that ...
     *  This method will notify in last-in-first-out (LIFO) order
     *
     *  @param event   the event to send to the listener
     */
    public synchronized void fireContestInfo(ContestInfoEvent event) {
        // Fire the event to all listeners (done in reverse order from how they were added).
        for (int i = contestInfoListeners.size() - 1; i >= 0; i--) {
            ContestInfoListener listener = (ContestInfoListener) contestInfoListeners.get(i);
            listener.contestInfo(event);
        }
    }


}

/* @(#)ContestInfoSupport.java */
