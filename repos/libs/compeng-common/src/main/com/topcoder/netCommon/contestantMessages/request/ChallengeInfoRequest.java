package com.topcoder.netCommon.contestantMessages.request;

import java.io.IOException;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a request to get the information of a user's solution. The information is used to challenge the solution of
 * the user, such as the data types of the arguments and the point cost of success/failure challenge.<br>
 * Use: This request is sent by the client before the current user enters the challenge arguments.<br>
 * Note: This is request not used any more.
 * 
 * @author Walter Mundt
 * @version $Id: ChallengeInfoRequest.java 72143 2008-08-06 05:54:59Z qliu $
 */
public class ChallengeInfoRequest extends BaseRequest {
    /** Represents the handle of the user who is being challenged. */
    protected String defender;

    /** Represents the problem component ID of the being challenged solution. */
    protected int componentID;

    /**
     * Creates a new instance of <code>ChallengeInfoRequest</code>. It is required by custom serialization.
     */
    public ChallengeInfoRequest() {
    }

    /**
     * Creates a new instance of <code>ChallengeInfoRequest</code>.
     * 
     * @param defender the handle of the user being challenged.
     * @param componentID the problem component ID of the solution.
     */
    public ChallengeInfoRequest(String defender, int componentID) {
        this.defender = defender;
        this.componentID = componentID;
    }

    /**
     * Gets the problem component ID of the being challenged solution.
     * 
     * @return the problem component ID.
     */
    public int getComponentID() {
        return componentID;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeString(defender);
        writer.writeInt(componentID);
    }

    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        defender = reader.readString();
        componentID = reader.readInt();
    }

    public int getRequestType() {
        return ContestConstants.CHALLENGE_INFO;
    }

    /**
     * Gets the handle of the user being challenged.
     * 
     * @return the handle of the user being challenged.
     */
    public String getDefender() {
        return defender;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.request.ChallengeInfoRequest) [");
        ret.append("defender = ");
        if (defender == null) {
            ret.append("null");
        } else {
            ret.append(defender.toString());
        }
        ret.append(", ");
        ret.append("componentID = ");
        ret.append(componentID);
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }
}
