package com.topcoder.netCommon.contestantMessages.response;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines an abstract response containing the information of a room. It contains information about the type of the room
 * and the ID of the room.
 * 
 * @author Lars Backstrom
 * @version $Id: WatchableResponse.java 72300 2008-08-13 08:33:29Z qliu $
 */
public abstract class WatchableResponse extends BaseResponse {
    /** Represents the type of the room. */
    private int roomType;

    /** Represents the ID of the room. */
    private int roomID;

    /**
     * Creates a new instance of <code>WatchableResponse</code>.
     * 
     * @param roomType the type of the room.
     * @param roomID the ID of the room.
     */
    public WatchableResponse(int roomType, int roomID) {
        this.roomType = roomType;
        this.roomID = roomID;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.response.WatchableResponse) [");
        ret.append("roomType = ");
        ret.append(roomType);
        ret.append(", ");
        ret.append("roomID = ");
        ret.append(roomID);
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeInt(roomType);
        writer.writeInt(roomID);
    }

    /**
     * Gets the type of the room.
     * 
     * @return the room type.
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
     * Sets the ID of the room.
     * 
     * @param roomID the room ID.
     */
    public void setRoomID(int roomID) {
        this.roomID = roomID;
    }

    /**
     * Sets the type of the room.
     * 
     * @param roomType the room type.
     */
    public void setRoomType(int roomType) {
        this.roomType = roomType;
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        roomType = reader.readInt();
        roomID = reader.readInt();
    }
}
