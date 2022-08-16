package com.topcoder.client.spectatorApp.scoreboard.model;

/**
 * RoomManager.java
 *
 * Description:		The manager of rooms
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Category;

import com.topcoder.client.spectatorApp.event.DefineRoomEvent;
import com.topcoder.client.spectatorApp.event.RoomAdapter;
import com.topcoder.client.spectatorApp.event.RoomWinnerEvent;
import com.topcoder.client.spectatorApp.netClient.SpectatorEventProcessor;

public class RoomManager {

    /** Reference to the logging category */
    private static final Category cat = Category.getInstance(RoomManager.class.getName());

    /** Singleton instance */
    private static RoomManager roomManager = null;

    /** Handler for room definitions */
    private RoomHandler roomHandler = new RoomHandler();


    /** List holding all the rooms */
    private HashMap rooms = new HashMap();

    /**
     * Constructor of a Room manager.  This registers a room listener witht the event processor
     */
    private RoomManager() {
        // Register the room handler as a listener
        SpectatorEventProcessor.getInstance().addRoomListener(roomHandler);

    }

    /**
     * Retreives the singleton instance
     * @returns RoomManager the singleton room manager
     */
    public static synchronized RoomManager getInstance() {
        if (roomManager == null) roomManager = new RoomManager();
        return roomManager;
    }

    /**
     * Disposes of any resources used
     */
    public void dispose() {
        // Removes the room handler as a listener
        SpectatorEventProcessor.getInstance().removeRoomListener(roomHandler);

        // Allow each room to dispose of any resources
        for (Iterator itr = rooms.values().iterator(); itr.hasNext();) {
            ((Room) itr.next()).dispose();
        }
    }

    /**
     * Returns the room matching the roomID.  Returns null if not found
     * @param roomID the roomID to find
     */
    public Room getRoom(int roomID) {
        return (Room) rooms.get(new Integer(roomID));
    }

    /**
     * Returns the first room
     * @return the first room or null if none
     */
    public Room getFirstRoom() {
        if (rooms.size() == 0) return null;
        return (Room) rooms.values().iterator().next();
    }

    /** Class handling the define room messages */
    private class RoomHandler extends RoomAdapter {

        public void defineRoom(DefineRoomEvent evt) {

            // Create the room
            Room room;
            try {
                room = new Room(evt.getRoomID(), evt.getRoomType(), evt.getRoomTitle(), evt.getRoundID(), evt.getCoders(), evt.getProblems());
            } catch (InstantiationException e) {
                cat.error("Error creating the room " + evt.getRoomID(), e);
                return;
            }

            // Dispose of the old room if it was defined
            Room oldRoom = getRoom(evt.getRoomID());
            if (oldRoom != null) oldRoom.dispose();

            // Add the room to the map
            rooms.put(new Integer(evt.getRoomID()), room);
        }

        public void roomWinner(RoomWinnerEvent evt) {
            Room room = (Room) rooms.get(new Integer(evt.getRoomID()));
            if (room == null) {
                cat.info("Unknown room: " + evt.getRoomID());
            } else {
                room.setWinner(evt.getPlayerHandle());
            }
        }

    }
}

