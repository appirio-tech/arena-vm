package com.topcoder.netCommon.contestantMessages.response;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.util.Arrays;

import com.topcoder.netCommon.contestantMessages.response.data.RoomData;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a response to send the list of contest rooms in a round.<br>
 * Use: When receiving this response, the client should update the mechanism for the current user to choose a room to
 * enter in a round. All previous rooms in the same round should be replaced by this new list.<br>
 * Note: The response is sent directly by server without corresponding request. It can be sent during phase changing, or
 * loading/updating a contest.
 * 
 * @author Lars Backstrom
 * @version $Id: CreateRoomListResponse.java 72300 2008-08-13 08:33:29Z qliu $
 */
public class CreateRoomListResponse extends BaseResponse {
    /** Represents the ID of the round. */
    private long roundID;

    /** Represents the admin room of the round if available. */
    private RoomData adminRoom;

    /** Represents the contest rooms of the round. */
    private RoomData[] coderRooms;

    /**
     * Creates a new instance of <code>CreateRoomListResponse</code>. It is required by custom serialization.
     */
    public CreateRoomListResponse() {
    }

    /**
     * Creates a new instance of <code>CreateRoomListResponse</code>. There is no copy. The round does not have an
     * admin room.
     * 
     * @param roundID the ID of the round.
     * @param coderRooms the contest rooms of the round.
     */
    public CreateRoomListResponse(long roundID, RoomData[] coderRooms) {
        this.roundID = roundID;
        this.coderRooms = coderRooms;
    }

    /**
     * Creates a new instance of <code>CreateRoomListResponse</code>. There is no copy.
     * 
     * @param roundID the ID of the round.
     * @param coderRooms the contest rooms of the round.
     * @param adminRoom the admin room of the round.
     */
    public CreateRoomListResponse(long roundID, RoomData[] coderRooms, RoomData adminRoom) {
        this(roundID, coderRooms);
        this.adminRoom = adminRoom;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeLong(roundID);
        writer.writeObject(adminRoom);
        writer.writeObjectArray(coderRooms);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        roundID = reader.readLong();
        adminRoom = (RoomData) reader.readObject();
        coderRooms = (RoomData[]) reader.readObjectArray(RoomData.class);
    }

    /**
     * Gets the ID of the round.
     * 
     * @return the round ID.
     */
    public long getRoundID() {
        return roundID;
    }

    /**
     * Gets the contest rooms of the round. There is no copy.
     * 
     * @return the contest rooms of the round.
     */
    public RoomData[] getCoderRooms() {
        return coderRooms;
    }

    /**
     * Gets the admin room of the round if available. When there is no admin room, <code>null</code> is returned.
     * 
     * @return the admin room of the round.
     */
    public RoomData getAdminRoom() {
        return adminRoom;
    }

    /**
     * Gets a flag indicating if the round has an admin room.
     * 
     * @return <code>true</code> if there is an admin room; <code>false</code> otherwise.
     */
    public boolean hasAdminRoom() {
        return adminRoom != null;
    }

    public String toString() {
        return "(CreateRoomListResponse)[ roundID = " + roundID + ", adminRoom = " + adminRoom + ", coderRooms = "
            + Arrays.asList(coderRooms) + "]";
    }
}