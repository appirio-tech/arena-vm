package com.topcoder.netCommon.contestantMessages.request;

import java.io.IOException;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a request to notify the server that the current user finishes the process of moving into a room.<br>
 * Use: When the client receives the room information, it should send this message to finalize the movement. The server
 * will reply with current users in the room.<br>
 * Note: A movement is not necessarily initiated by the client. The server may forcely move the user to another room by
 * sending room information.
 * 
 * @author Walter Mundt
 * @version $Id: EnterRequest.java 72163 2008-08-07 07:51:04Z qliu $
 * @see MoveRequest
 * @see EnterRoundRequest
 * @see com.topcoder.netCommon.contest.response.RoomInfoResponse
 */
public class EnterRequest extends BaseRequest {
    /** Represents the ID of the room which the current user just entered. */
    protected int roomID;

    /**
     * Creates a new instance of <code>EnterRequest</code>. It is required by custom serialization.
     */
    public EnterRequest() {
    }

    /**
     * Creates a new instance of <code>EnterRequest</code>.
     * 
     * @param roomID the ID of the room which the current user just entered.
     */
    public EnterRequest(int roomID) {
        this.roomID = roomID;
    }

    public int getRequestType() {
        return ContestConstants.ENTER;
    }

    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        roomID = reader.readInt();
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeInt(roomID);
    }

    /**
     * Gets the ID of the room which the user just entered.
     * 
     * @return the ID of the room.
     */
    public int getRoomID() {
        return roomID;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.request.EnterRequest) [");
        ret.append("roomID = ");
        ret.append(roomID);
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }
}
