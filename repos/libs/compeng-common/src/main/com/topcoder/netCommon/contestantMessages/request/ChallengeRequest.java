package com.topcoder.netCommon.contestantMessages.request;

import java.io.IOException;
import java.util.ArrayList;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a request to challenge a solution.<br>
 * Use: This request is sent after the current user enters the challenge arguments and confirms to challenge the
 * solution.<br>
 * Note: The challenge result may not return immediately. Even if the response times out, the challenge is still
 * processed and any points adjustment will happen.
 * 
 * @author Walter Mundt
 * @version $Id: ChallengeRequest.java 72143 2008-08-06 05:54:59Z qliu $
 */
public class ChallengeRequest extends BaseRequest {
    /** Represents the the problem component ID of the solution to be challenged. */
    protected int componentID;

    /** Represents the challenge arguments. */
    protected ArrayList test;

    /** Represents the handle of the user to be challenged. */
    protected String defender;

    /**
     * Creates a new instance of <code>ChallengeRequest</code>. It is required by custom serialization.
     */
    public ChallengeRequest() {
    }

    /**
     * Creates a new instance of <code>ChallengeRequest</code>. There is no copy on the list.
     * 
     * @param componentID the problem component ID of the solution to be challenged.
     * @param test the challenge arguments.
     * @param defender the handle of the user to be challenged.
     */
    public ChallengeRequest(int componentID, ArrayList test, String defender) {
        this.componentID = componentID;
        this.test = test;
        this.defender = defender;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeInt(componentID);
        writer.writeArrayList(test);
        writer.writeString(defender);
    }

    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        componentID = reader.readInt();
        test = reader.readArrayList();
        defender = reader.readString();
    }

    public int getRequestType() {
        return ContestConstants.CHALLENGE;
    }

    /**
     * Gets the problem component ID of the solution to be challenged.
     * 
     * @return the problem component ID.
     */
    public int getComponentID() {
        return componentID;
    }

    /**
     * Gets the challenge arguments. There is no copy.
     * 
     * @return the list of challenge arguments.
     */
    public ArrayList getTest() {
        return test;
    }

    /**
     * Gets the handle of the user to be challenged.
     * 
     * @return the handle of the user to be challenged.
     */
    public String getDefender() {
        return defender;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.request.ChallengeRequest) [");
        ret.append("componentID = ");
        ret.append(componentID);
        ret.append(", ");
        ret.append("test = ");
        if (test == null) {
            ret.append("null");
        } else {
            ret.append(test.toString());
        }
        ret.append(", ");
        ret.append("defender = ");
        if (defender == null) {
            ret.append("null");
        } else {
            ret.append(defender.toString());
        }
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }
}
