/**
 * ProblemSupport.java
 *
 * Description:		Event set support class for ProblemListener.
 Manages listener registration and contains fire functions.
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.event;


import java.util.ArrayList;

/**
 * ProblemSupport bottlenecks support for classes that fire events to
 * ProblemListener listeners.
 */

public class ProblemSupport {

    /** Holder for all listeners */
    private ArrayList problemListeners = new ArrayList();

    /**
     * Adds a listener
     *
     * @param listener the listener to be added
     */
    public synchronized void addProblemListener(ProblemListener listener) {
        // add a listener if it is not already registered
        if (!problemListeners.contains(listener)) {
            problemListeners.add(listener);
        }
    }

    /**
     * Removes a listener
     *
     * @param listener the listener to be removed
     */
    public synchronized void removeProblemListener(ProblemListener listener) {
        // remove it if it is registered
        int pos = problemListeners.indexOf(listener);
        if (pos >= 0) {
            problemListeners.remove(pos);
        }
    }

    /**
     *  Notifies all listeners that a problem was opened
     *
     *  @param event   the event to send to the listener
     */
    public synchronized void fireOpened(ProblemNotificationEvent event) {
        // Fire the event to all listeners (done in reverse order from how they were added).
        for (int i = problemListeners.size() - 1; i >= 0; i--) {
            ProblemListener listener = (ProblemListener) problemListeners.get(i);
            listener.opened(event);
        }
    }

    /**
     *  Notifies all listeners that a problem was closed
     *
     *  @param event   the event to send to the listener
     */
    public synchronized void fireClosed(ProblemNotificationEvent event) {
        // Fire the event to all listeners (done in reverse order from how they were added).
        for (int i = problemListeners.size() - 1; i >= 0; i--) {
            ProblemListener listener = (ProblemListener) problemListeners.get(i);
            listener.closed(event);
        }
    }

    /**
     *  Notifies all listeners that a problem is being compiled
     *
     *  @param event   the event to send to the listener
     */
    public synchronized void fireCompiling(ProblemNotificationEvent event) {
        // Fire the event to all listeners (done in reverse order from how they were added).
        for (int i = problemListeners.size() - 1; i >= 0; i--) {
            ProblemListener listener = (ProblemListener) problemListeners.get(i);
            listener.compiling(event);
        }
    }

    /**
     *  Notifies all listeners that a problem is being tested
     *
     *  @param event   the event to send to the listener
     */
    public synchronized void fireTesting(ProblemNotificationEvent event) {
        // Fire the event to all listeners (done in reverse order from how they were added).
        for (int i = problemListeners.size() - 1; i >= 0; i--) {
            ProblemListener listener = (ProblemListener) problemListeners.get(i);
            listener.testing(event);
        }
    }

    /**
     *  Notifies all listeners that a problem is being submitted
     *
     *  @param event   the event to send to the listener
     */
    public synchronized void fireSubmitting(ProblemNotificationEvent event) {
        // Fire the event to all listeners (done in reverse order from how they were added).
        for (int i = problemListeners.size() - 1; i >= 0; i--) {
            ProblemListener listener = (ProblemListener) problemListeners.get(i);
            listener.submitting(event);
        }
    }

    /**
     *  Notifies all listeners that a problem is being challenged
     *
     *  @param event   the event to send to the listener
     */
    public synchronized void fireChallenging(ProblemNotificationEvent event) {
        // Fire the event to all listeners (done in reverse order from how they were added).
        for (int i = problemListeners.size() - 1; i >= 0; i--) {
            ProblemListener listener = (ProblemListener) problemListeners.get(i);
            listener.challenging(event);
        }
    }

    /**
     *  Notifies all listeners that a problem is being system tested
     *
     *  @param event   the event to send to the listener
     */
    public synchronized void fireSystemTesting(ProblemNotificationEvent event) {
        // Fire the event to all listeners (done in reverse order from how they were added).
        for (int i = problemListeners.size() - 1; i >= 0; i--) {
            ProblemListener listener = (ProblemListener) problemListeners.get(i);
            listener.systemTesting(event);
        }
    }
    
    public synchronized void fireLongProblemInfo(LongProblemNotificationEvent event) {
        // Fire the event to all listeners (done in reverse order from how they were added).
        for (int i = problemListeners.size() - 1; i >= 0; i--) {
            ProblemListener listener = (ProblemListener) problemListeners.get(i);
            listener.longProblemInfo(event);
        }
    }


}

/* @(#)ProblemSupport.java */
