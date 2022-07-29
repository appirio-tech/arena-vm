/**
 * RoomWinnerEvent.java
 *
 * Description:		Contains information pertaining to who won the room
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.event;

public class RoomWinnerEvent extends RoomEvent {

    /** Player who has won the room */
    private String handle;

    /**
     *  Constructor of a Room Event
     *
     *  @param source the source of the event
     *  @param roomID the unique identifier of a room
     *  @param playerID the player who has won the room
     *
     */
    public RoomWinnerEvent(Object source, int roomID, String handle) {
        super(source, roomID);
        this.handle = handle;
    }


    /**
     * Returns the room winner.
     * @return String
     */
    public String getPlayerHandle() {
        return handle;
    }


}


/* @(#)RoomWinnerEvent.java */
