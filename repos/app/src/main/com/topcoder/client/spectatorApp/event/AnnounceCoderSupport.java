/**
 * AnnounceCoderSupport.java
 *
 * Description:		Event set support class for AnnounceCoderListener.
 Manages listener registration and contains fire functions.
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.event;


import java.util.ArrayList;

/**
 * AnnounceCoderSupport bottlenecks support for classes that fire events to
 * AnnounceCoderListener listeners.
 */

public class AnnounceCoderSupport implements java.io.Serializable {

    /** Holder for all listeners */
    private ArrayList announceCoderListeners = new ArrayList();

    /**
     * Adds a listener
     *
     * @param listener the listener to be added
     */
    public synchronized void addAnnounceCoderListener(AnnounceCoderListener listener) {
        // add a listener if it is not already registered
        if (!announceCoderListeners.contains(listener)) {
            announceCoderListeners.add(listener);
        }
    }

    /**
     * Removes a listener
     *
     * @param listener the listener to be removed
     */
    public synchronized void removeAnnounceCoderListener(AnnounceCoderListener listener) {
        // remove it if it is registered
        int pos = announceCoderListeners.indexOf(listener);
        if (pos >= 0) {
            announceCoderListeners.remove(pos);
        }
    }

    /**
     *  Notifies all listeners of a coder announcement
     *  This method will notify in last-in-first-out (LIFO) order
     *
     *  @param event   the event to send to the listener
     */
    public synchronized void fireAnnounceCoder(AnnounceCoderEvent event) {
        // Fire the event to all listeners (done in reverse order from how they were added).
        for (int i = announceCoderListeners.size() - 1; i >= 0; i--) {
            AnnounceCoderListener listener = (AnnounceCoderListener) announceCoderListeners.get(i);
            listener.announceCoder(event);
        }
    }


}

/* @(#)AnnounceCoderSupport.java */
