/**
 * RoomSupport.java
 *
 * Description:		Event set support class for RoomListener.
 Manages listener registration and contains fire functions.
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.event;


import java.util.ArrayList;

/**
 * RoomSupport bottlenecks support for classes that fire events to
 * RoomListener listeners.
 */

public class RoomSupport {

    /** Holder for all listeners */
    private ArrayList roomListeners = new ArrayList();

    /**
     * Adds a listener
     *
     * @param listener the listener to be added
     */
    public synchronized void addRoomListener(RoomListener listener) {
        // add a listener if it is not already registered
        if (!roomListeners.contains(listener)) {
            roomListeners.add(listener);
        }
    }

    /**
     * Removes a listener
     *
     * @param listener the listener to be removed
     */
    public synchronized void removeRoomListener(RoomListener listener) {
        // remove it if it is registered
        int pos = roomListeners.indexOf(listener);
        if (pos >= 0) {
            roomListeners.remove(pos);
        }
    }

    /**
     *  Notifies all listeners that it should define a specific room
     *
     *  @param event   the event to send to the listener
     */
    public synchronized void fireDefineRoom(DefineRoomEvent event) {
        // Fire the event to all listeners (done in reverse order from how they were added).
        for (int i = roomListeners.size() - 1; i >= 0; i--) {
            RoomListener listener = (RoomListener) roomListeners.get(i);
            listener.defineRoom(event);
        }
    }

    /**
     *  Notifies all listeners to show a specific room
     *
     *  @param event   the event to send to the listener
     */
    public synchronized void fireShowRoom(ShowRoomEvent event) {
        // Fire the event to all listeners (done in reverse order from how they were added).
        for (int i = roomListeners.size() - 1; i >= 0; i--) {
            RoomListener listener = (RoomListener) roomListeners.get(i);
            listener.showRoom(event);
        }
    }

    /**
     *  Notifies all listeners that a room winner wasfound
     *
     *  @param event   the event to send to the listener
     */
    public synchronized void fireRoomWinner(RoomWinnerEvent event) {
        // Fire the event to all listeners (done in reverse order from how they were added).
        for (int i = roomListeners.size() - 1; i >= 0; i--) {
            RoomListener listener = (RoomListener) roomListeners.get(i);
            listener.roomWinner(event);
        }
    }
}

/* @(#)RoomSupport.java */
