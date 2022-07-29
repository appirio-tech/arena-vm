package com.topcoder.netCommon.contestantMessages.request;

import java.io.IOException;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a request to notify the server to send the divison summary updates of a round to the client.<br>
 * Use: When the current user opens the division summary window for a round, this request should be sent.<br>
 * Note: The current user does not need to be in a room of the round. This request <b>will not</b> automatically
 * unsubscribe any prior division summary subscription. Any prior division summary subscription should be unsubscribed
 * before sending this request.
 * 
 * @author Ryan Fairfax
 * @version $Id: DivSummaryRequest.java 72163 2008-08-07 07:51:04Z qliu $
 */
public class DivSummaryRequest extends BaseRequest {
    /** Represents the ID of the round whose division summary is subscribed. */
    protected int roundID;

    /** Represents the division of the round whose summary is subscribed. */
    protected int divisionID;

    /**
     * Creates a new instance of <code>DivSummaryRequest</code>. It is required by custom serialization.
     */
    public DivSummaryRequest() {
    }

    /**
     * Creates a new instance of <code>DivSummaryRequest</code>.
     * 
     * @param roundID the ID of the round whose division summary is subscribed.
     * @param divisionID the division of the round whose summary is subscribed.
     */
    public DivSummaryRequest(int roundID, int divisionID) {
        this.roundID = roundID;
        this.divisionID = divisionID;
    }

    /**
     * Gets the ID of the round whose division summary is subscribed.
     * 
     * @return the ID of the round.
     */
    public int getRoundID() {
        return roundID;
    }

    /**
     * Sets the ID of the round whose division summary is subscribed.
     * 
     * @param roundID the ID of the round.
     */
    public void setRoundID(int roundID) {
        this.roundID = roundID;
    }

    /**
     * Gets the division of the round whose summary is subscribed.
     * 
     * @return the division of the round.
     */
    public int getDivisionID() {
        return divisionID;
    }

    /**
     * Sets the division of the round whose summary is subscribed.
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
        ret.append("(com.topcoder.netCommon.contestantMessages.request.DivSummaryRequest) [");
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
