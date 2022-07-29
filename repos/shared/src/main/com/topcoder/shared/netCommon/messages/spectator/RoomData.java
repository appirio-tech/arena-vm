/**
 * RoomData.java
 *
 * Description:		Structure containing information about a room
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.shared.netCommon.messages.spectator;

import com.topcoder.shared.netCommon.*;

import java.io.*;

public class RoomData implements Serializable, Cloneable, CustomSerializable {

    /** Identifier of the room */
    private int roomID;

    /** Type of room */
    private int roomType;

    /** Title of the room */
    private String roomTitle;

    /** The ID of the round */
    private int roundID;

    /** RoomData type for a lobby room */
    public final static int LOBBY = 1;

    /** RoomData type for a scoreboard room */
    public final static int SCOREBOARD = 2;

    /** RoomData type for a coding room  */
    public final static int CODING = 3;


    /**
     * No-arg constructor needed by customserialization
     *
     */
    public RoomData() {
    }

    /**
     * Constructor of a RoomData with a blank title
     *
     * @param roomID   the identifier of the room
     * @param roomType the type of room
     */
    public RoomData(int roomID, int roomType) {
        this(roomID, roomType, "", 0);
    }

    /**
     * Constructor of a RoomData
     *
     * @param roomID      the identifier of the room
     * @param roomType    the type of room
     * @param roomTitle   the title for the room
     * @param roundID     the identifier of the round associated with this room
     */
    public RoomData(int roomID, int roomType, String roomTitle, int roundID) {
        this.roomID = roomID;
        this.roomType = roomType;
        this.roomTitle = roomTitle;
        this.roundID = roundID;
    }

    /**
     GT ADDED TO FIX COMPILE ERRORS FOR NOW
     */
    public RoomData(int roomID, int roomType, String roomTitle) {
        this(roomID, roomType, roomTitle, 0);
    }


    /**
     * Gets the roomID
     *
     * @return the unique identifier of the room
     */
    public int getRoomID() {
        return roomID;
    }

    /**
     * Gets the type of room
     *
     * @return the type of room
     */
    public int getRoomType() {
        return roomType;
    }


    /**
     * Gets the title of the room
     *
     * @return the title of the room
     */
    public String getRoomTitle() {
        return roomTitle;
    }

    /**
     * Gets the identifier of the round associated with this room
     *
     * @return the round identifier
     */
    public int getRoundID() {
        return roundID;
    }

    /**
     * Serializes the object
     *
     * @param writer the custom serialization writer
     * @throws java.io.IOException exception during writing
     *
     * @see com.topcoder.shared.netCommon.CSWriter
     * @see java.io.IOException
     */
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(roomID);
        writer.writeInt(roomType);
        writer.writeString(roomTitle);
        writer.writeInt(roundID);
    }

    /**
     * Creates the object from a serialization stream
     *
     * @param reader the custom serialization reader
     * @throws java.io.IOException           exception during reading
     * @throws java.io.ObjectStreamException exception during reading
     *
     * @see com.topcoder.shared.netCommon.CSWriter
     * @see java.io.IOException
     * @see java.io.ObjectStreamException
     */
    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        roomID = reader.readInt();
        roomType = reader.readInt();
        roomTitle = reader.readString();
        roundID = reader.readInt();
    }

    /**
     * Gets the string representation of this object
     *
     * @return the string representation of this object
     */
    public String toString() {
        return new StringBuffer().append("(RoomData)[").append(roomID).append(", ").append(roomType).append(", ").append(roomTitle).append(", ").append(roundID).append("]").toString();
    }
}


/* @(#)RoomData.java */
