/**
 * TimerSupport.java
 *
 * Description:		Event set support class for TimerListener.
 Manages listener registration and contains fire functions.
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.event;


import java.util.ArrayList;

/**
 * TimerSupport bottlenecks support for classes that fire events to
 * TimerListener listeners.
 */

public class TimerSupport {

    /** Holder for all listeners */
    private ArrayList timerListeners = new ArrayList();

    /**
     * Adds a listener
     *
     * @param listener the listener to be added
     */
    public synchronized void addTimerListener(TimerListener listener) {
        // add a listener if it is not already registered
        if (!timerListeners.contains(listener)) {
            timerListeners.add(listener);
        }
    }

    /**
     * Removes a listener
     *
     * @param listener the listener to be removed
     */
    public synchronized void removeTimerListener(TimerListener listener) {
        // remove it if it is registered
        int pos = timerListeners.indexOf(listener);
        if (pos >= 0) {
            timerListeners.remove(pos);
        }
    }

    /**
     *  Notifies all listeners that the timer was updated
     *
     *  @param event   the event to send to the listener
     */
    public synchronized void fireTimerUpdate(TimerEvent event) {
        // Fire the event to all listeners (done in reverse order from how they were added).
        for (int i = timerListeners.size() - 1; i >= 0; i--) {
            TimerListener listener = (TimerListener) timerListeners.get(i);
            listener.timerUpdate(event);
        }
    }


}

/* @(#)TimerSupport.java */
