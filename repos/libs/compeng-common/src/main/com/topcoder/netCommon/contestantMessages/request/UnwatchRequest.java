package com.topcoder.netCommon.contestantMessages.request;

import java.io.IOException;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a request to notify the server to start sending the updates of a room to the client, including chat messages.<br>
 * Use: When the current user stops monitoring a room, this request should be sent.<br>
 * Note: It should be sent when the room messages are subscribed.
 * 
 * @author Walter Mundt
 * @version $Id: UnwatchRequest.java 72292 2008-08-12 09:10:29Z qliu $
 * @see UnwatchRequest
 */
public class UnwatchRequest extends BaseRequest {
    /** Represents the ID of the room to be stopped monitoring. */
    protected int roomID;

    /**
     * Creates a new instance of <code>UnwatchRequest</code>. It is required by custom serialization.
     */
    public UnwatchRequest() {
    }

    /**
     * Creates a new instance of <code>UnwatchRequest</code>.
     * 
     * @param roomID the ID of the room to be stopped monitoring.
     */
    public UnwatchRequest(int roomID) {
        this.roomID = roomID;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeInt(roomID);
    }

    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        roomID = reader.readInt();
    }

    /**
     * Gets the ID of the room to be stopped monitoring.
     * 
     * @return the room ID.
     */
    public int getRoomID() {
        return roomID;
    }

    public int getRequestType() {
        return ContestConstants.UNWATCH;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.request.UnwatchRequest) [");
        ret.append("roomID = ");
        ret.append(roomID);
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }
}
