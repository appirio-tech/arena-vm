/**
 * RoomWinnerEvent.java
 *
 * Description:		Contains information pertaining to who won the room
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.event;


public class ShowRoomEvent extends RoomEvent {

    /**
     *  Constructor of a Room Event
     *
     *  @param source the source of the event
     *  @param roomID the unique identifier of a room
     *
     */
    public ShowRoomEvent(Object source, int roomID) {
        super(source, roomID);
    }


}


/* @(#)RoomWinnerEvent.java */
