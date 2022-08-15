package com.topcoder.netCommon.contestantMessages.request;

import java.io.IOException;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a request to notify the server to stop sending the summary updates of a room to the client.<br>
 * Use: When the current user closes the room summary window, this request should be sent.<br>
 * Note: The current user must be in the room whose summary is closed. The room summary must be subscribed prior sending
 * this request. This message is deprecated.
 * 
 * @author Lars Backstrom
 * @version $Id: CloseSummaryRequest.java 72833 2008-09-17 07:33:19Z qliu $
 * @see OpenSummaryRequest
 */
public class CloseSummaryRequest extends BaseRequest {
    /** Represents the ID of the room whose summary is being closed. */
    int roomID;

    /**
     * Creates a new instance of <code>CloseSummaryRequest</code>. It is required by custom serialization.
     */
    public CloseSummaryRequest() {
    }

    /**
     * Creates a new instance of <code>CloseSummaryRequest</code>.
     * 
     * @param roomID the ID of the room whose summary is being closed.
     */
    public CloseSummaryRequest(int roomID) {
        this.roomID = roomID;
    }

    public int getRequestType() {
        return ContestConstants.CLOSE_SUMMARY_REQUEST;
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
     * Gets the ID of the room whose summary is being closed.
     * 
     * @return the ID of the room.
     */
    public int getRoomID() {
        return roomID;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.request.CloseSummaryRequest) [");
        ret.append("roomID = ");
        ret.append(roomID);
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }
}
