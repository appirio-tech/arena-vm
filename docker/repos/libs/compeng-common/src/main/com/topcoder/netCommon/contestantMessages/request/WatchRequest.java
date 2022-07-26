package com.topcoder.netCommon.contestantMessages.request;

import java.io.IOException;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a request to notify the server to start sending the updates of a room to the client, including chat messages.<br>
 * Use: This request is able to let the current user monitor a room without actually entering to it, for example, if you
 * double click the room leader in the leader board window in TopCoder arena applet.<br>
 * Note: The current user will not be able to chat in the subscribed room. It must be certain that there is no other
 * room being subscribed when this message is sent.
 * 
 * @author Walter Mundt
 * @version $Id: WatchRequest.java 72292 2008-08-12 09:10:29Z qliu $
 * @see UnwatchRequest
 */
public class WatchRequest extends BaseRequest {
    /** Represents the ID of the room to be monitored. */
    protected int roomID;

    /**
     * Creates a new instance of <code>WatchRequest</code>. It is required by custom serialization.
     */
    public WatchRequest() {
    }

    /**
     * Creates a new instance of <code>WatchRequest</code>.
     * 
     * @param roomID the ID of the room to be monitored.
     */
    public WatchRequest(int roomID) {
        this.roomID = roomID;
    }

    /**
     * Gets the ID of the room to be monitored.
     * 
     * @return the room ID.
     */
    public int getRoomID() {
        return roomID;
    }

    /**
     * Sets the ID of the room to be monitored.
     * 
     * @param roomID the room ID.
     */
    public void setRoomID(int roomID) {
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

    public int getRequestType() {
        return ContestConstants.WATCH;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.request.WatchRequest) [");
        ret.append("roomID = ");
        ret.append(roomID);
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }
}
