package com.topcoder.netCommon.contestantMessages.request;

import java.io.IOException;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a request to initiate the round registration procedure (such as checking the eligibility) and send back
 * survey data (including terms of use).<br>
 * Use: When the current user clicks the register button in a round, this request should be sent, not
 * <code>RegisterRequest</code>.<br>
 * Note: The round must be in registration phase.
 * 
 * @author Walter Mundt
 * @version $Id: RegisterInfoRequest.java 72292 2008-08-12 09:10:29Z qliu $
 * @see RegisterRequest
 */
public class RegisterInfoRequest extends BaseRequest {
    /** Represents the ID of the round to be registered. */
    int roundID;

    /**
     * Creates a new instance of <code>RegisterInfoRequest</code>. It is required by custom serialization.
     */
    public RegisterInfoRequest() {
    }

    /**
     * Creates a new instance of <code>RegisterInfoRequest</code>.
     * 
     * @param roundID the ID of the round to be registered.
     */
    public RegisterInfoRequest(int roundID) {
        this.roundID = roundID;
    }

    public int getRequestType() {
        return ContestConstants.REGISTER_INFO;
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
     * Gets the ID of the round to be registered.
     * 
     * @return the round ID.
     */
    public int getRoundID() {
        return roundID;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.request.RegisterInfoRequest) [");
        ret.append("roundID = ");
        ret.append(roundID);
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }
}
