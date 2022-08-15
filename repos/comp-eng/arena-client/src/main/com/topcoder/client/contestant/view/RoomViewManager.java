/*
 * Author: Michael Cervantes (emcee) Date: Jun 4, 2002 Time: 8:25:15 PM
 */
package com.topcoder.client.contestant.view;

import com.topcoder.client.contestant.RoomModel;

/**
 * Defines a manager which manages the models of rooms.
 * 
 * @author Michael Cervantes
 * @version $Id: RoomViewManager.java 72032 2008-07-30 06:28:49Z qliu $
 */
public interface RoomViewManager {
    /**
     * Sets the model of the room where the current user is located.
     * 
     * @param room the model of the current room.
     */
    public void setCurrentRoom(RoomModel room);

    /**
     * Adds a room model to the manager. It is caused by a new room received from the server.
     * 
     * @param room the room model to be added.
     */
    public void addRoom(RoomModel room);

    /**
     * Removes a room model from the manager. It is caused by a remove request received from the server.
     * 
     * @param room the room model to be added.
     */
    public void removeRoom(RoomModel room);

    /**
     * Clears all room models in the manager. It is caused by a remove request received from the server.
     */
    public void clearRooms();
}
