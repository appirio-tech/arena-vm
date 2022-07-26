/**
 * RegisterRoomRequest.java Description: A request by a spectator for regular status updates for a room
 * 
 * @author Dave Pecora
 * @version 1.0
 */

package com.topcoder.netCommon.contestantMessages.request;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

import java.io.IOException;
import java.io.ObjectStreamException;

/**
 * Defines a request to notify the server to start sending all events of a room to the client.<br>
 * Use: This request is used by scoreboard application to render the current status of a contest room.<br>
 * Note: The arena applet should never use this request.
 * 
 * @author Dave Pecora
 * @version $Id: RegisterRoomRequest.java 72292 2008-08-12 09:10:29Z qliu $
 */
public class RegisterRoomRequest extends BaseRequest {
    /** Represents the ID of the room to be subscribed. */
    int roomID;

    /**
     * Creates a new instance of <code>RegisterRoomRequest</code>. It is required by custom serialization.
     */
    public RegisterRoomRequest() {
    }

    /**
     * Creates a new instance of <code>RegisterRoomRequest</code>.
     * 
     * @param roomID the ID of the room to be subscribed.
     */
    public RegisterRoomRequest(int roomID) {
        this.roomID = roomID;
    }

    /**
     * Gets the room ID requested by the spectator.
     * 
     * @return the room ID
     */
    public int getRoomID() {
        return roomID;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeInt(roomID);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        roomID = reader.readInt();
    }

    public String toString() {
        return "(com.topcoder.netCommon.contestantMessages.request.RegisterRoomRequest) [roomID = " + roomID + "]";
    }

    public int getRequestType() {
        return ContestConstants.REGISTER_ROOM;
    }
}
