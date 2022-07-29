package com.topcoder.netCommon.contestantMessages.request;

import java.io.IOException;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a request to get the list of all registrants in a round.<br>
 * Use: When the current user wants to see all registrants in a round, this request is sent.<br>
 * Note: The round must be an active contest round.
 * 
 * @author Walter Mundt
 * @version $Id: RegisterUsersRequest.java 72292 2008-08-12 09:10:29Z qliu $
 */
public class RegisterUsersRequest extends BaseRequest {
    /** Represents the ID of the round whose registrants are requested. */
    int roundID;

    /**
     * Creates a new instance of <code>RegisterUsersRequest</code>. It is required by custom serialization.
     */
    public RegisterUsersRequest() {
    }

    /**
     * Creates a new instance of <code>RegisterUsersRequest</code>.
     * 
     * @param roundID the ID of the round whose registrants are requested.
     */
    public RegisterUsersRequest(int roundID) {
        this.roundID = roundID;
    }

    public int getRequestType() {
        return ContestConstants.REGISTER_USERS;
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
     * Gets the ID of the round whose registrants are requested.
     * 
     * @return the round ID.
     */
    public int getRoundID() {
        return roundID;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.request.RegisterUsersRequest) [");
        ret.append("roundID = ");
        ret.append(roundID);
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }
}
