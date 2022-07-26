/**
 * Just had to name it this way - read it as a Manager of RoomViewManager(s)
 * 
 * @author Tim 'Pops' Roberts
 * @since May 30, 2003
 */
package com.topcoder.client.contestant;

import java.util.ArrayList;
import java.util.List;

import com.topcoder.client.contestant.view.RoomViewManager;

/**
 * Manages room view listeners. The room view listeners are called when there is a change to the active room list. This
 * class is thread-safe.
 * 
 * @author Tim 'Pops' Roberts
 * @version $Id: RoomViewManagerManager.java 71977 2008-07-28 12:55:54Z qliu $
 */
public class RoomViewManagerManager {
    /** Represents the list of all room view listeners. */
    private List listeners = new ArrayList();

    /**
     * Creates a new instance of <code>RoomViewManagerManager</code>. Initially, there is no listener.
     */
    public RoomViewManagerManager() {
    }

    /**
     * Notifies all listeners that the logged in coder is moving to the given room.
     * 
     * @param room the room that coder is moving to.
     */
    public synchronized void setCurrentRoom(RoomModel room) {
        for (int idx = 0; idx < listeners.size(); idx++) {
            RoomViewManager rv = (RoomViewManager) listeners.get(idx);
            rv.setCurrentRoom(room);
        }
    }

    /**
     * Notifies all listeners that a room is added to the active room list.
     * 
     * @param room the room to be added.
     */
    public synchronized void addRoom(RoomModel room) {
        for (int idx = 0; idx < listeners.size(); idx++) {
            RoomViewManager rv = (RoomViewManager) listeners.get(idx);
            rv.addRoom(room);
        }
    }

    /**
     * Notifies all listeners that a room is removed to the active room list.
     * 
     * @param room the room to be removed.
     */
    public synchronized void removeRoom(RoomModel room) {
        for (int idx = 0; idx < listeners.size(); idx++) {
            RoomViewManager rv = (RoomViewManager) listeners.get(idx);
            rv.removeRoom(room);
        }
    }

    /**
     * Notifies all listeners that the active room list is cleared.
     */
    public synchronized void clearRooms() {
        for (int idx = 0; idx < listeners.size(); idx++) {
            RoomViewManager rv = (RoomViewManager) listeners.get(idx);
            rv.clearRooms();
        }
    }

    /**
     * Adds a room view listener to this manager.
     * 
     * @param listener the room view listener to be added.
     */
    public synchronized void addListener(RoomViewManager listener) {
        listeners.add(listener);
    }

    /**
     * Removes a room view listener from this manager.
     * 
     * @param listener the room view listener to be removed.
     */
    public synchronized void removeListener(RoomViewManager listener) {
        while (listeners.remove(listener)) {
        }
    }

    /**
     * Gets all room view listeners in the manager. A copy is returned.
     * 
     * @return a list of all room view listeners.
     */
    public synchronized List getListeners() {
        return new ArrayList(listeners);
    }
}
