/**
 * CoderMoveSupport.java
 *
 * Description:		Event set support class for CoderMoveListener.
 Manages listener registration and contains fire functions.
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.scoreboard.model;


import java.util.ArrayList;

/**
 * CoderMoveSupport bottlenecks support for classes that fire events to
 * CoderMoveListener listeners.
 */

public class CoderMoveSupport implements java.io.Serializable {

    /** Holder for all listeners */
    private ArrayList coderMoveListeners = new ArrayList();

    /**
     * Adds a listener
     *
     * @param listener the listener to be added
     */
    public synchronized void addCoderMoveListener(CoderMoveListener listener) {
        // add a listener if it is not already registered
        if (!coderMoveListeners.contains(listener)) {
            coderMoveListeners.add(listener);
        }
    }

    /**
     * Removes a listener
     *
     * @param listener the listener to be removed
     */
    public synchronized void removeCoderMoveListener(CoderMoveListener listener) {
        // remove it if it is registered
        int pos = coderMoveListeners.indexOf(listener);
        if (pos >= 0) {
            coderMoveListeners.remove(pos);
        }
    }

    /**
     *  Notifies all listeners that a problem was opened
     *  This method will notify in last-in-first-out (LIFO) order
     *
     *  @param event   the event to send to the listener
     */
    public synchronized void fireProblemOpened(CoderMoveEvent event) {
        // Fire the event to all listeners (done in reverse order from how they were added).
        for (int i = coderMoveListeners.size() - 1; i >= 0; i--) {
            com.topcoder.client.spectatorApp.scoreboard.model.CoderMoveListener listener = (com.topcoder.client.spectatorApp.scoreboard.model.CoderMoveListener) coderMoveListeners.get(i);
            listener.problemOpened(event);
        }
    }

    /**
     *  Notifies all listeners that a problem was closed
     *  This method will notify in last-in-first-out (LIFO) order
     *
     *  @param event   the event to send to the listener
     */
    public synchronized void fireProblemClosed(CoderMoveEvent event) {
        // Fire the event to all listeners (done in reverse order from how they were added).
        for (int i = coderMoveListeners.size() - 1; i >= 0; i--) {
            com.topcoder.client.spectatorApp.scoreboard.model.CoderMoveListener listener = (com.topcoder.client.spectatorApp.scoreboard.model.CoderMoveListener) coderMoveListeners.get(i);
            listener.problemClosed(event);
        }
    }


}

/* @(#)CoderMoveSupport.java */
