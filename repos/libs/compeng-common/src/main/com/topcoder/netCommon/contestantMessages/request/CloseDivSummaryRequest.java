package com.topcoder.netCommon.contestantMessages.request;

import java.io.IOException;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a request to notify the server to stop sending division summary updates of a round to the client.<br>
 * Use: When the current user closes the division summary window, this request should be sent.<br>
 * Note: The current user does not need to be in a room of the round. It should be sent when the division summary is
 * subscribed.
 * 
 * @author Ryan Fairfax
 * @version $Id: CloseDivSummaryRequest.java 72163 2008-08-07 07:51:04Z qliu $
 */
public class CloseDivSummaryRequest extends BaseRequest {
    /** Represents the ID of the round. */
    protected int roundID;

    /** Represents the division of the round. */
    protected int divisionID;

    /**
     * Creates a new instance of <code>CloseDivSummaryRequest</code>. It is required by custom serialization.
     */
    public CloseDivSummaryRequest() {
    }

    /**
     * Creates a new instance of <code>CloseDivSummaryRequest</code>.
     * 
     * @param roundID the ID of the round whose division summary is unsubscribed.
     * @param divisionID the division whose summary is unsubscribed.
     */
    public CloseDivSummaryRequest(int roundID, int divisionID) {
        this.roundID = roundID;
        this.divisionID = divisionID;
    }

    /**
     * Gets the ID of the round.
     * 
     * @return the ID of the round.
     */
    public int getRoundID() {
        return roundID;
    }

    /**
     * Sets the ID of the round.
     * 
     * @param roundID the ID of the round.
     */
    public void setRoundID(int roundID) {
        this.roundID = roundID;
    }

    /**
     * Gets the division of the round.
     * 
     * @return the division of the round.
     */
    public int getDivisionID() {
        return divisionID;
    }

    /**
     * Sets the division of the round.
     * 
     * @param divisionID the division of the round.
     */
    public void setDivisionID(int divisionID) {
        this.divisionID = divisionID;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeInt(roundID);
        writer.writeInt(divisionID);
    }

    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        roundID = reader.readInt();
        divisionID = reader.readInt();
    }

    public int getRequestType() {
        return ContestConstants.WATCH;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.request.CloseDivSummaryRequest) [");
        ret.append("roundID = ");
        ret.append(roundID);
        ret.append(", ");
        ret.append("divisionID = ");
        ret.append(divisionID);
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }
}
