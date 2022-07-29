package com.topcoder.netCommon.contestantMessages.request;

import java.io.IOException;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a request to clear a group of problem components in a practice room.<br>
 * Use: This request is sent when the current user wants to clear the score(s) and solution(s) of one or more problem
 * components in a practice room.<br>
 * Note: Only practice room can be cleared. The user must be in the practice room.
 * 
 * @author Ryan Fairfax
 * @version $Id: ClearPracticeProblemRequest.java 72143 2008-08-06 05:54:59Z qliu $
 */
public class ClearPracticeProblemRequest extends BaseRequest {
    /** Represents the ID of the practice room where the scores and solutions needs to be cleared. */
    int roomID;

    /** Represents the IDs of the problem components to be cleared. */
    Long[] componentID;

    /**
     * Creates a new instance of <code>ClearPracticeProblemRequest</code>. It is required by custom serialization.
     */
    public ClearPracticeProblemRequest() {
    }

    /**
     * Creates a new instance of <code>ClearPracticeProblemRequest</code>.
     * 
     * @param roomID the ID of the practice room.
     * @param componentID the IDs of the problem components to be cleared.
     */
    public ClearPracticeProblemRequest(int roomID, Long[] componentID) {
        this.roomID = roomID;
        this.componentID = componentID;
    }

    /**
     * Gets the ID of the practice room.
     * 
     * @return the ID of the practice room.
     */
    public int getRoomID() {
        return roomID;
    }

    /**
     * Gets the IDs of the problem components to be cleared.
     * 
     * @return the IDs of the problem components.
     */
    public Long[] getComponentID() {
        return componentID;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeInt(roomID);
        writer.writeInt(componentID.length);
        for (int i = 0; i < componentID.length; i++) {
            writer.writeLong(componentID[i].longValue());
        }
    }

    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        roomID = reader.readInt();
        int len = reader.readInt();
        componentID = new Long[len];
        for (int i = 0; i < len; i++) {
            componentID[i] = new Long(reader.readLong());
        }
    }

    public int getRequestType() {
        return ContestConstants.CLEAR_PRACTICE_PROBLEM;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.request.ClearPracticeProblemRequest) [");
        ret.append("roomID = ");
        ret.append(roomID);
        ret.append(", ");
        ret.append("componentID = ");
        ret.append(componentID);
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }
}