/**
 * VoteSupport.java
 *
 * Description:		Event set support class for VoteListener
 Manages listener registration and contains fire functions.
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.event;

import java.util.ArrayList;

/**
 * VoteSupport bottlenecks support for classes that fire events to
 * VoteSupport listeners.
 */

public class VoteSupport implements java.io.Serializable {

    /** Holder for all listeners */
    private ArrayList VoteListeners = new ArrayList();

    /**
     * Adds a listener
     *
     * @param listener the listener to be added
     */
    public synchronized void addVoteListener(VoteListener listener) {
        // add a listener if it is not already registered
        if (!VoteListeners.contains(listener)) {
            VoteListeners.add(listener);
        }
    }

    /**
     * Removes a listener
     *
     * @param listener the listener to be removed
     */
    public synchronized void removeVoteListener(VoteListener listener) {
        // remove it if it is registered
        int pos = VoteListeners.indexOf(listener);
        if (pos >= 0) {
            VoteListeners.remove(pos);
        }
    }

    /**
     *  Notifies all listeners that a vote was cast
     *  This method will notify in last-in-first-out (LIFO) order
     *
     *  @param event   the event to send to the listener
     */
    public synchronized void fireVotedFor(VoteEvent event) {
        // Fire the event to all listeners (done in reverse order from how they were added).
        for (int i = VoteListeners.size() - 1; i >= 0; i--) {
            VoteListener listener = (VoteListener) VoteListeners.get(i);
            listener.votedFor(event);
        }
    }

    /**
     *  Notifies all listeners that someone was voted out
     *  This method will notify in last-in-first-out (LIFO) order
     *
     *  @param event   the event to send to the listener
     */
    public synchronized void fireVotedOut(VoteEvent event) {
        // Fire the event to all listeners (done in reverse order from how they were added).
        for (int i = VoteListeners.size() - 1; i >= 0; i--) {
            VoteListener listener = (VoteListener) VoteListeners.get(i);
            listener.votedOut(event);
        }
    }

}

/* @(#)ContestInfoSupport.java */
