/**
 * PointValueChangeSupport.java
 *
 * Description:		Event set support class for PointValueChangeListener.
 Manages listener registration and contains fire functions.
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.scoreboard.model;


import java.util.ArrayList;

/**
 * PointValueChangeSupport bottlenecks support for classes that fire events to
 * PointValueChangeListener listeners.
 */

public class PointValueChangeSupport implements java.io.Serializable {

    /** Holder for all listeners */
    private ArrayList pointValueChangeListeners = new ArrayList();

    /**
     * Adds a listener
     *
     * @param listener the listener to be added
     */
    public synchronized void addPointValueChangeListener(PointValueChangeListener listener) {
        // add a listener if it is not already registered
        if (!pointValueChangeListeners.contains(listener)) {
            pointValueChangeListeners.add(listener);
        }
    }

    /**
     * Removes a listener
     *
     * @param listener the listener to be removed
     */
    public synchronized void removePointValueChangeListener(PointValueChangeListener listener) {
        // remove it if it is registered
        int pos = pointValueChangeListeners.indexOf(listener);
        if (pos >= 0) {
            pointValueChangeListeners.remove(pos);
        }
    }

    /**
     * Fires notifications off to all listeners (in reverse order)
     *
     * @param evt the point value change event
     */
    public synchronized void fireUpdatePointValue(PointValueChangeEvent evt) {
        // Fire the event to all listeners (done in reverse order from how they were added).
        for (int i = pointValueChangeListeners.size() - 1; i >= 0; i--) {
            PointValueChangeListener listener = (PointValueChangeListener) pointValueChangeListeners.get(i);
            listener.updatePointValue(evt);
        }
    }


}

/* @(#)PointValueChangeSupport.java */
