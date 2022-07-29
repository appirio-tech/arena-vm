package com.topcoder.netCommon.contestantMessages.request;

import java.io.IOException;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a request to get the schedule of a round.<br>
 * Use: This request is sent when the current user wants to see the schedule of an active round.<br>
 * Note: This request is deprecated.
 * 
 * @author Michael Cervantes
 * @version $Id: RoundScheduleRequest.java 72834 2008-09-17 07:38:41Z qliu $
 */
public class RoundScheduleRequest extends BaseRequest {
    /** Represents the ID of the round whose schedule is requested. */
    long roundID;

    /**
     * Creates a new instance of <code>RoundScheduleRequest</code>. It is required by custom serialization.
     */
    public RoundScheduleRequest() {
    }

    /**
     * Creates a new instance of <code>RoundScheduleRequest</code>.
     * 
     * @param roundID the ID of the round whose schedule is requested.
     */
    public RoundScheduleRequest(long roundID) {
        this.roundID = roundID;
    }

    public int getRequestType() {
        return ContestConstants.REGISTER_USERS;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeLong(roundID);
    }

    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        roundID = reader.readLong();
    }

    /**
     * Gets the ID of the round whose schedule is requested.
     * 
     * @return the round ID.
     */
    public long getRoundID() {
        return roundID;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.request.RoundScheduleRequest) [");
        ret.append("roundID = ");
        ret.append(roundID);
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }
}
