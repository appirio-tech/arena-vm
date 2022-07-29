/**
 * RoomEvent.java
 *
 * Description:		Contains information pertaining to a specific room
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.event;


public class RoomEvent extends java.util.EventObject {

    /** Identifier of the room */
    private int roomID;

    /**
     *  Constructor of a Room Event
     *
     *  @param source the source of the event
     *  @param roomID the unique identifier of a room
     *
     *  @see com.topcoder.netCommon.contest.ContestConstants
     */
    public RoomEvent(Object source, int roomID) {
        super(source);
        this.roomID = roomID;
    }


    /**
     * Returns the roomID.
     * @return int
     */
    public int getRoomID() {
        return roomID;
    }

}


/* @(#)RoomEvent.java */
