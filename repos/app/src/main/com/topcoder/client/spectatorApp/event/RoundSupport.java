/**
 * RoundSupport.java
 *
 * Description:		Event set support class for RoundListener.
 Manages listener registration and contains fire functions.
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.event;

import java.util.ArrayList;

/**
 * RoundSupport bottlenecks support for classes that fire events to
 * RoundSupport listeners.
 */

public class RoundSupport implements java.io.Serializable {

    /** Holder for all listeners */
    private ArrayList roundListeners = new ArrayList();

    /**
     * Adds a listener
     *
     * @param listener the listener to be added
     */
    public synchronized void addRoundListener(RoundListener listener) {
        // add a listener if it is not already registered
        if (!roundListeners.contains(listener)) {
            roundListeners.add(listener);
        }
    }

    /**
     * Removes a listener
     *
     * @param listener the listener to be removed
     */
    public synchronized void removeRoundListener(RoundListener listener) {
        // remove it if it is registered
        int pos = roundListeners.indexOf(listener);
        if (pos >= 0) {
            roundListeners.remove(pos);
        }
    }

    /**
     *  Notifies all listeners that a define round event happened
     *  This method will notify in last-in-first-out (LIFO) order
     *
     *  @param event   the event to send to the listener
     */
    public synchronized void fireDefineRound(RoundEvent event) {
        // Fire the event to all listeners (done in reverse order from how they were added).
        for (int i = roundListeners.size() - 1; i >= 0; i--) {
            RoundListener listener = (RoundListener) roundListeners.get(i);
            listener.defineRound(event);
        }
    }

    /**
     *  Notifies all listeners that a define round event happened
     *  This method will notify in last-in-first-out (LIFO) order
     *
     *  @param event   the event to send to the listener
     */
    public synchronized void fireShowRound(RoundEvent event) {
        // Fire the event to all listeners (done in reverse order from how they were added).
        for (int i = roundListeners.size() - 1; i >= 0; i--) {
            RoundListener listener = (RoundListener) roundListeners.get(i);
            listener.showRound(event);
        }
    }

}

/* @(#)ContestInfoSupport.java */
