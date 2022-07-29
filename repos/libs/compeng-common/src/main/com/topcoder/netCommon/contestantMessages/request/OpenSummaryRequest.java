package com.topcoder.netCommon.contestantMessages.request;

import java.io.IOException;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a request to notify the server to start sending the summary updates of a room to the client.<br>
 * Use: When the current user opens the room summary window, this request should be sent.<br>
 * Note: The current user must be in the room whose summary is open. The room summary must be unsubscribed prior sending
 * this request. This message is deprecated.
 * 
 * @author Lars Backstrom
 * @version $Id: OpenSummaryRequest.java 72833 2008-09-17 07:33:19Z qliu $
 * @see CloseSummaryRequest
 */
public class OpenSummaryRequest extends BaseRequest {
    /** Represents the ID of the room whose summary is being open. */
    int roomID;

    /**
     * Creates a new instance of <code>OpenSummaryRequest</code>. It is required by custom serialization.
     */
    public OpenSummaryRequest() {
    }

    /**
     * Creates a new instance of <code>CloseSummaryRequest</code>.
     * 
     * @param roomID the ID of the room whose summary is being open.
     */
    public OpenSummaryRequest(int roomID) {
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
        return ContestConstants.OPEN_SUMMARY_REQUEST;
    }

    /**
     * Gets the ID of the room whose summary is being open.
     * 
     * @return the ID of the room.
     */
    public int getRoomID() {
        return roomID;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.request.OpenSummaryRequest) [");
        ret.append("roomID = ");
        ret.append(roomID);
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }
}
