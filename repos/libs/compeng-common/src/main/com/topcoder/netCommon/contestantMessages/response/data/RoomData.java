/**
 * RoomData.java Description: Structure containing information about a room
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */

package com.topcoder.netCommon.contestantMessages.response.data;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contestantMessages.response.CreateRoomListResponse;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

/**
 * Defines a room in a division of a round.
 * 
 * @author Tim "Pops" Roberts
 * @version $Id: RoomData.java 72424 2008-08-20 08:06:01Z qliu $
 * @see CreateRoomListResponse
 */
public class RoomData implements Serializable, Cloneable, CustomSerializable {
    /** Represents the ID of the room. */
    private int roomID;

    /** Represents the type of the room. */
    private int roomType;

    /** Represents the name of the room. */
    private String roomTitle;

    /** Represents the ID of the round which the room belongs to. */
    private int roundID;

    /** Represents the division of the round which the room belongs to. */
    private int divisionID;

    /**
     * Creates a new instance of <code>RoomData</code>. It is required by custom serialization.
     */
    public RoomData() {
    }

    /**
     * Creates a new instance of <code>RoomData</code>.
     * 
     * @param roomID the ID of the room.
     * @param roomType the type of room.
     * @param roomTitle the name of the room.
     * @param roundID the ID of the round which the room belongs to.
     * @param divisionID the division of the round which the room belongs to.
     * @see #getRoomType()
     */
    public RoomData(int roomID, int roomType, String roomTitle, int roundID, int divisionID) {
        this.roomID = roomID;
        this.roomType = roomType;
        this.roomTitle = roomTitle;
        this.roundID = roundID;
        this.divisionID = divisionID;
    }

    /**
     * Gets the ID of the room.
     * 
     * @return the room ID.
     */
    public int getRoomID() {
        return roomID;
    }

    /**
     * Gets the type of room.
     * 
     * @return the type of room.
     * @see ContestConstants#CODER_ROOM
     * @see ContestConstants#TEAM_CODER_ROOM
     * @see ContestConstants#PRACTICE_CODER_ROOM
     * @see ContestConstants#TEAM_PRACTICE_CODER_ROOM
     * @see ContestConstants#ADMIN_ROOM
     * @see ContestConstants#TEAM_ADMIN_ROOM
     */
    public int getRoomType() {
        return roomType;
    }

    /**
     * Gets the name of the room.
     * 
     * @return the name of the room.
     */
    public String getRoomTitle() {
        return roomTitle;
    }

    /**
     * Gets the ID of the round which the room belongs to.
     * 
     * @return the round ID.
     */
    public int getRoundID() {
        return roundID;
    }

    /**
     * Gets the division of the round which the room belongs to.
     * 
     * @return the division of the round.
     */
    public int getDivisionID() {
        return divisionID;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(roomID);
        writer.writeInt(roomType);
        writer.writeString(roomTitle);
        writer.writeInt(roundID);
        writer.writeInt(divisionID);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        roomID = reader.readInt();
        roomType = reader.readInt();
        roomTitle = reader.readString();
        roundID = reader.readInt();
        divisionID = reader.readInt();
    }

    public String toString() {
        return new StringBuffer().append("(RoomData)[").append(roomID).append(", ").append(roomType).append(", ")
            .append(roomTitle).append(", ").append(roundID).append(", ").append(divisionID).append("]").toString();
    }
}

/* @(#)RoomData.java */
