package com.topcoder.netCommon.contestantMessages.request;

import java.io.IOException;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a request to view the system test results after submission during coding phase.<br>
 * Use: This request is designed for special onsite events (such as Sun JavaOne onsite). In such event, users are
 * allowed to do system tests and see the results immediately after submission during the coding phase.<br>
 * Note: To use this feature, it must be enabled on the server-side. The user must be registered in the round.
 * 
 * @author Ryan Fairfax
 * @version $Id: SystestResultsRequest.java 72292 2008-08-12 09:10:29Z qliu $
 */
public final class SystestResultsRequest extends BaseRequest {
    /** Represents the ID of the round whose system test results are requested. */
    int roundID;

    /**
     * Creates a new instance of <code>SystestResultsRequest</code>. It is required by custom serialization.
     */
    public SystestResultsRequest() {
    }

    /**
     * Creates a new instance of <code>SystestResultsRequest</code>.
     * 
     * @param roundID the ID of the round whose system test results are requested.
     */
    public SystestResultsRequest(int roundID) {
        this.roundID = roundID;
    }

    public int getRequestType() {
        return ContestConstants.SYSTEST_RESULTS;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeInt(roundID);
    }

    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        roundID = reader.readInt();
    }

    /**
     * Gets the ID of the round whose system test results are requested.
     * 
     * @return the round ID.
     */
    public int getRoundID() {
        return roundID;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.request.SystestResultsRequest) [");
        ret.append("roundID = ");
        ret.append(roundID);
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }
}
