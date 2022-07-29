package com.topcoder.netCommon.contestantMessages.request;

import java.io.IOException;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a request to initiate the movement of the current user to another room. The request will result the server to
 * send the room information together with phase information, problem components, users assigned to this room, etc (if
 * applicable).<br>
 * Use: When the current user wants to move to another lobby/contest room/practice room, this request should be sent.<br>
 * Note: Loading the room information might take seconds to minutes depending on the size of the room and the network
 * bandwidth. It is recommended to use asynchronized way to send this request and display certain intermission UI when
 * waiting for the response.
 * 
 * @author Walter Mundt
 * @version $Id: MoveRequest.java 72163 2008-08-07 07:51:04Z qliu $
 * @see EnterRequest
 */
public class MoveRequest extends BaseRequest {
    /** Represents the type of the room to be moved to. */
    protected int moveType;

    /** Represents the ID of the room to be moved to. */
    protected int roomID;

    /**
     * Creates a new instance of <code>MoveRequest</code>. It is required by custom serialization.
     */
    public MoveRequest() {
    }

    /**
     * Creates a new instance of <code>MoveRequest</code>.
     * 
     * @param moveType the type of the room to be moved to.
     * @param roomID the ID of the room to be moved to.
     * @see #getRoomID()
     * @see #getMoveType()
     */
    public MoveRequest(int moveType, int roomID) {
        this.moveType = moveType;
        this.roomID = roomID;
    }

    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        moveType = reader.readInt();
        roomID = reader.readInt();
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeInt(moveType);
        writer.writeInt(roomID);
    }

    public int getRequestType() {
        return ContestConstants.MOVE;
    }

    /**
     * Gets the type of the room to be moved to.
     * 
     * @return the type of the room to be moved to.
     * @see ContestConstants#ADMIN_ROOM
     * @see ContestConstants#CODER_ROOM
     * @see ContestConstants#TEAM_CODER_ROOM
     * @see ContestConstants#SPECTATOR_ROOM
     * @see ContestConstants#TEAM_ADMIN_ROOM
     * @see ContestConstants#TEAM_PRACTICE_CODER_ROOM
     * @see ContestConstants#PRACTICE_CODER_ROOM
     * @see ContestConstants#PRACTICE_SPECTATOR_ROOM
     * @see ContestConstants#WATCH_ROOM
     * @see ContestConstants#LOGIN_ROOM
     * @see ContestConstants#LOBBY_ROOM
     * @see ContestConstants#MODERATED_CHAT_ROOM
     */
    public int getMoveType() {
        return moveType;
    }

    /**
     * Gets the ID of the room to be moved to. If the ID is <code>ContestConstants.ANY_ROOM</code>, an available room
     * of the type specified in the request will be chosen.
     * 
     * @return the ID of the room.
     * @see ContestConstants#ANY_ROOM
     */
    public int getRoomID() {
        return roomID;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.request.MoveRequest) [");
        ret.append("moveType = ");
        ret.append(moveType);
        ret.append(", ");
        ret.append("roomID = ");
        ret.append(roomID);
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }
}
