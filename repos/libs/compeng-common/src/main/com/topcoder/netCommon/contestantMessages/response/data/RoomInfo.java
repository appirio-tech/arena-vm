package com.topcoder.netCommon.contestantMessages.response.data;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contestantMessages.response.EnterRoomResponse;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

/**
 * Defines a room in a division of a round.
 * 
 * @author Matthew P. Suhocki (msuhocki)
 * @version $Id: RoomInfo.java 72424 2008-08-20 08:06:01Z qliu $
 * @see EnterRoomResponse
 * @deprecated It is not used.
 */
public class RoomInfo implements Serializable, CustomSerializable {
    /** Represents the ID of the round which the room belongs to. */
    int roundID;

    /** Represents the ID of the room. */
    int roomID;

    /** Represents the type of the room. */
    int roomType;

    /** Represents the name of the contest which the round belongs to. */
    String contestName;

    /** Represents the name of the round which the room belongs to. */
    String roundName;

    /** Represents the name of the room. */
    String roomName;

    /**
     * Creates a new instance of <code>RoomInfo</code>. It is required by custom serialization.
     */
    public RoomInfo() {
    }

    /**
     * Creates a new instance of <code>RoomInfo</code>.
     * 
     * @param roundID the ID of the round which the room belongs to.
     * @param roomID the ID of the room.
     * @param roomType the type of the room.
     * @param contestName the name of the contest which the round belongs to.
     * @param roundName the name of the round which the room belongs to.
     * @param roomName
     * @see #getRoomType()
     */
    public RoomInfo(int roundID, int roomID, int roomType, String contestName, String roundName, String roomName) {
        setRoundID(roundID);
        setRoomID(roomID);
        setRoomType(roomType);
        setContestName(contestName);
        setRoundName(roundName);
        setRoomName(roomName);
    }

    public void customWriteObject(CSWriter csWriter) throws IOException {
        csWriter.writeInt(getRoundID());
        csWriter.writeInt(getRoomID());
        csWriter.writeInt(getRoomType());
        csWriter.writeString(getContestName());
        csWriter.writeString(getRoundName());
        csWriter.writeString(getRoomName());
    }

    public void customReadObject(CSReader csReader) throws IOException, ObjectStreamException {
        setRoundID(csReader.readInt());
        setRoomID(csReader.readInt());
        setRoomType(csReader.readInt());
        setContestName(csReader.readString());
        setRoundName(csReader.readString());
        setRoomName(csReader.readString());
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
     * Gets the ID of the room.
     * 
     * @return the room ID.
     */
    public int getRoomID() {
        return roomID;
    }

    /**
     * Gets the type of the room.
     * 
     * @return the type of the room.
     * @see ContestConstants#LOGIN_ROOM
     * @see ContestConstants#SPECTATOR_ROOM
     * @see ContestConstants#CODER_ROOM
     * @see ContestConstants#TEAM_CODER_ROOM
     * @see ContestConstants#LOBBY_ROOM
     * @see ContestConstants#PRACTICE_CODER_ROOM
     * @see ContestConstants#TEAM_PRACTICE_CODER_ROOM
     * @see ContestConstants#PRACTICE_SPECTATOR_ROOM
     * @see ContestConstants#WATCH_ROOM
     * @see ContestConstants#MODERATED_CHAT_ROOM
     * @see ContestConstants#ADMIN_ROOM
     * @see ContestConstants#TEAM_ADMIN_ROOM
     * @see ContestConstants#CONTEST_ROOM
     */
    public int getRoomType() {
        return roomType;
    }

    /**
     * Gets the name of the contest which the round belongs to.
     * 
     * @return the contest name.
     */
    public String getContestName() {
        return contestName;
    }

    /**
     * Gets the name of the round which the room belongs to.
     * 
     * @return the round name.
     */
    public String getRoundName() {
        return roundName;
    }

    /**
     * Gets the name of the room.
     * 
     * @return the room name.
     */
    public String getRoomName() {
        return roomName;
    }

    /**
     * Sets the ID of the round which the room belongs to.
     * 
     * @param roundID the round ID.
     */
    public void setRoundID(int roundID) {
        this.roundID = roundID;
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
     * @param roomType the type of the room.
     * @see #getRoomType()
     */
    public void setRoomType(int roomType) {
        this.roomType = roomType;
    }

    /**
     * Sets the name of the contest which the round belongs to.
     * 
     * @param contestName the contest name.
     */
    public void setContestName(String contestName) {
        this.contestName = contestName;
    }

    /**
     * Sets the name of the round which the room belongs to.
     * 
     * @param roundName the round name.
     */
    public void setRoundName(String roundName) {
        this.roundName = roundName;
    }

    /**
     * Sets the name of the room.
     * 
     * @param roomName the room name.
     */
    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.response.data.RoomInfo) [");
        ret.append("roundID = ");
        ret.append(roundID);
        ret.append(", ");
        ret.append("roomID = ");
        ret.append(roomID);
        ret.append(", ");
        ret.append("roomType = ");
        ret.append(roomType);
        ret.append(", ");
        ret.append("contestName = ");
        if (contestName == null) {
            ret.append("null");
        } else {
            ret.append(contestName.toString());
        }
        ret.append(", ");
        ret.append("roundName = ");
        if (roundName == null) {
            ret.append("null");
        } else {
            ret.append(roundName.toString());
        }
        ret.append(", ");
        ret.append("roomName = ");
        if (roomName == null) {
            ret.append("null");
        } else {
            ret.append(roomName.toString());
        }
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }
}
