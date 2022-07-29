/**
 * RoomEvent.java
 *
 * Description:		Contains information pertaining to a specific room
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.event;

import java.util.List;

public class DefineRoomEvent extends RoomEvent {

    /** Type of room */
    private int roomType;

    /** Title of the room */
    private String roomTitle;

    /** The round the room is part of*/
    private int roundID;

    /** List of coders assigned to the room */
    private List coders;

    /** List of problems assigned to the room */
    private List problems;


    /**
     *  Constructor of a Room Event
     *
     *  @param source the source of the event
     *  @param roomID the unique identifier of a room
     *  @param roomType the type of room
     *  @param roomTitle the title of the room
     *  @param roundID the round the room is part of
     *  @param coders the coders assigned to the room (should be all Coder objects)
     *  @param problems the problems assigned to the room (should be all Problem objects)
     *
     */
    public DefineRoomEvent(Object source, int roomID, int roomType, String roomTitle, int roundID, List coders, List problems) {
        super(source, roomID);
        this.roomType = roomType;
        this.roomTitle = roomTitle;
        this.roundID = roundID;
        this.coders = coders;
        this.problems = problems;
    }


    /**
     * Returns the coders.
     * @return List
     */
    public List getCoders() {
        return coders;
    }

    /**
     * Returns the problems.
     * @return List
     */
    public List getProblems() {
        return problems;
    }

    /**
     * Returns the roomTitle.
     * @return String
     */
    public String getRoomTitle() {
        return roomTitle;
    }

    /**
     * Returns the roomType.
     * @return int
     */
    public int getRoomType() {
        return roomType;
    }

    /**
     * Returns the roundID.
     * @return int
     */
    public int getRoundID() {
        return roundID;
    }

}


/* @(#)DefineRoomEvent.java */
