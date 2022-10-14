package com.topcoder.netCommon.contestantMessages.request;

import java.io.IOException;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a request to automatically execute system tests for the current user in a round.<br>
 * Use: For certain contests, user can request to execute system tests in the client. If so, the request is sent.<br>
 * Note: Its availability depends on the server setting. Unless specified, it is disabled.
 * 
 * @author Ryan Fairfax
 * @version $Id: AutoSystestRequest.java 72143 2008-08-06 05:54:59Z qliu $
 */
public final class AutoSystestRequest extends BaseRequest {
    /** Represents the ID of the round to be automatically system tested. */
    int roundID;

    /**
     * Creates a new instance of <code>AutoSystestRequest</code>. It is required by custom serialization.
     */
    public AutoSystestRequest() {
    }

    /**
     * Creates a new instance of <code>AutoSystestRequest</code>.
     * 
     * @param roundID the ID of the round to be system tested.
     */
    public AutoSystestRequest(int roundID) {
        this.roundID = roundID;
    }

    public int getRequestType() {
        return ContestConstants.AUTO_SYSTEST;
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
     * Gets the ID of the round to be system tested.
     * 
     * @return the ID of the round to be system tested.
     */
    public int getRoundID() {
        return roundID;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.request.AutoSystestRequest) [");
        ret.append("roundID = ");
        ret.append(roundID);
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }
}
