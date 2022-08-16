/**
 * ContestSupport.java
 *
 * Description:		Event set support class for ContestListener.
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

public class ContestSupport implements java.io.Serializable {

    /** Holder for all listeners */
    private ArrayList contestListeners = new ArrayList();

    /**
     * Adds a listener
     *
     * @param listener the listener to be added
     */
    public synchronized void addContestListener(ContestListener listener) {
        // add a listener if it is not already registered
        if (!contestListeners.contains(listener)) {
            contestListeners.add(listener);
        }
    }

    /**
     * Removes a listener
     *
     * @param listener the listener to be removed
     */
    public synchronized void removeContestListener(ContestListener listener) {
        // remove it if it is registered
        int pos = contestListeners.indexOf(listener);
        if (pos >= 0) {
            contestListeners.remove(pos);
        }
    }

    /**
     *  Notifies all listeners that ...
     *  This method will notify in last-in-first-out (LIFO) order
     *
     *  @param event   the event to send to the listener
     */
    public synchronized void fireDefineContest(ContestEvent event) {
        // Fire the event to all listeners (done in reverse order from how they were added).
        for (int i = contestListeners.size() - 1; i >= 0; i--) {
            ContestListener listener = (ContestListener) contestListeners.get(i);
            listener.defineContest(event);
        }
    }


}

/* @(#)ContestInfoSupport.java */
