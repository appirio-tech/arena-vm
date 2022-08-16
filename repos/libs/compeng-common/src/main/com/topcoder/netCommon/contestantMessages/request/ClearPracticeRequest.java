package com.topcoder.netCommon.contestantMessages.request;

import java.io.IOException;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a request to clear all problem components in a practice room.<br>
 * Use: This request is sent when the current user wants to clear the scores and solutions of all problem components in
 * a practice room.<br>
 * Note: Only practice room can be cleared. The user must be in the practice room.
 * 
 * @author Walter Mundt
 * @version $Id: ClearPracticeRequest.java 72143 2008-08-06 05:54:59Z qliu $
 * @see ClearPracticeProblemRequest
 */
public class ClearPracticeRequest extends BaseRequest {
    /** Represents the ID of the practice room where the scores and solutions needs to be cleared. */
    int roomID;

    /**
     * Creates a new instance of <code>ClearPracticeRequest</code>. It is required by custom serialization.
     */
    public ClearPracticeRequest() {
    }

    /**
     * Creates a new instance of <code>ClearPracticeRequest</code>.
     * 
     * @param roomID the ID of the practice room.
     */
    public ClearPracticeRequest(int roomID) {
        this.roomID = roomID;
    }

    /**
     * Gets the ID of the practice room.
     * 
     * @return the ID of the practice room.
     */
    public int getRoomID() {
        return roomID;
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
        return ContestConstants.CLEAR_PRACTICER;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.request.ClearPracticeRequest) [");
        ret.append("roomID = ");
        ret.append(roomID);
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }
}