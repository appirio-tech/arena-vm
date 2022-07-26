/**
 * ProblemResultSupport.java
 *
 * Description:		Event set support class for ProblemResultListener.
 Manages listener registration and contains fire functions.
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.event;


import java.util.ArrayList;

/**
 * ProblemResultSupport bottlenecks support for classes that fire events to
 * ProblemResultListener listeners.
 */

public class ProblemResultSupport {

    /** Holder for all listeners */
    private ArrayList problemResultListeners = new ArrayList();

    /**
     * Adds a listener
     *
     * @param listener the listener to be added
     */
    public synchronized void addProblemResultListener(ProblemResultListener listener) {
        // add a listener if it is not already registered
        if (!problemResultListeners.contains(listener)) {
            problemResultListeners.add(listener);
        }
    }

    /**
     * Removes a listener
     *
     * @param listener the listener to be removed
     */
    public synchronized void removeProblemResultListener(ProblemResultListener listener) {
        // remove it if it is registered
        int pos = problemResultListeners.indexOf(listener);
        if (pos >= 0) {
            problemResultListeners.remove(pos);
        }
    }

    /**
     *  Notifies all listeners the results of a submittion
     *
     *  @param event   the event to send to the listener
     */
    public synchronized void fireSubmitted(ProblemResultEvent event) {
        // Fire the event to all listeners (done in reverse order from how they were added).
        for (int i = problemResultListeners.size() - 1; i >= 0; i--) {
            ProblemResultListener listener = (ProblemResultListener) problemResultListeners.get(i);
            listener.submitted(event);
        }
    }

    /**
     *  Notifies all listeners the results of a challenge
     *
     *  @param event   the event to send to the listener
     */
    public synchronized void fireChallenged(ProblemResultEvent event) {
        // Fire the event to all listeners (done in reverse order from how they were added).
        for (int i = problemResultListeners.size() - 1; i >= 0; i--) {
            ProblemResultListener listener = (ProblemResultListener) problemResultListeners.get(i);
            listener.challenged(event);
        }
    }

    /**
     *  Notifies all listeners the results of a system test
     *
     *  @param event   the event to send to the listener
     */
    public synchronized void fireSystemTested(ProblemResultEvent event) {
        // Fire the event to all listeners (done in reverse order from how they were added).
        for (int i = problemResultListeners.size() - 1; i >= 0; i--) {
            ProblemResultListener listener = (ProblemResultListener) problemResultListeners.get(i);
            listener.systemTested(event);
        }
    }


}

/* @(#)ProblemResultSupport.java */
