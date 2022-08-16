package com.topcoder.netCommon.contestantMessages.response;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a response to send the information of a room. The information contains the ID of the room, the type of the
 * room, the name of the room, a description of the status of the room, and the ID of the round which the room belongs
 * to.<br>
 * Use: This response is sent as part of the responses of <code>MoveRequest</code> and <code>WatchRequest</code>.
 * The status description of the room is purely informational, and has no meaning towards the business logic.<br>
 * Note: The room may not belong to any round. In this case, the round ID is not available.
 * 
 * @author Lars Backstrom
 * @version $Id: RoomInfoResponse.java 72343 2008-08-15 06:09:22Z qliu $
 */
public class RoomInfoResponse extends BaseResponse {
    /** Represents the ID of the round which the room belongs to. */
    private Long roundID;

    /** Represents the type of the room. */
    private int roomType;

    /** Represents the ID of the room. */
    private int roomID;

    /** Represents the name of the room. */
    private String name;

    /** Represents the status description of the room. */
    private String status;

    /**
     * Creates a new instance of <code>RoomInfoResponse</code>. It is required by custom serialization.
     */
    public RoomInfoResponse() {
    }

    /**
     * Creates a new instance of <code>RoomInfoResponse</code>.
     * 
     * @param roundID the ID of the round which the room longs to.
     * @param roomType the type of the room.
     * @param roomID the ID of the room.
     * @param name the name of the room.
     * @param status the status description of the room.
     */
    public RoomInfoResponse(long roundID, int roomType, int roomID, String name, String status) {
        this.roundID = new Long(roundID);
        this.roomType = roomType;
        this.roomID = roomID;
        this.name = name;
        this.status = status;
    }

    /**
     * Creates a new instance of <code>RoomInfoResponse</code>. This constructor should be used when the room does
     * not belong to any round, since the round ID is uninitialized.
     * 
     * @param roomType the type of the room.
     * @param roomID the ID of the room.
     * @param name the name of the room.
     * @param status the status description of the room.
     */
    public RoomInfoResponse(int roomType, int roomID, String name, String status) {
        this.roomType = roomType;
        this.roomID = roomID;
        this.name = name;
        this.status = status;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeInt(roomType);
        writer.writeInt(roomID);
        writer.writeObject(roundID);
        writer.writeString(name);
        writer.writeString(status);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        roomType = reader.readInt();
        roomID = reader.readInt();
        roundID = (Long) reader.readObject();
        name = reader.readString();
        status = reader.readString();
    }

    /**
     * Gets the type of the room.
     * 
     * @return the type of the room.
     */
    public int getRoomType() {
        return roomType;
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
     * Gets a flag indicating if the room belongs to a round.
     * 
     * @return <code>true</code> if the room belongs to a round; <code>false</code> otherwise.
     */
    public boolean hasRoundID() {
        return roundID != null;
    }

    /**
     * Gets the ID of the round which the room belongs to.
     * 
     * @return the round ID.
     */
    public Long getRoundID() {
        return roundID;
    }

    /**
     * Gets the name of the room.
     * 
     * @return the name of the room.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the status description of the room.
     * 
     * @return the status description.
     */
    public String getStatus() {
        return status;
    }

    public String toString() {
        return "(RoomInfoResponse)[roomType = " + roomType + ", roomIndex = " + roomID + "]";
    }
}