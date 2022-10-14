package com.topcoder.netCommon.contestantMessages.request;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Requests that the contestant be added to the specfied team's available roster.<br>
 * Use: When the current user wants to join a team during a team-based round, this request is sent.
 * 
 * @author Matthew P. Suhocki
 * @version $Id: JoinTeamRequest.java 72292 2008-08-12 09:10:29Z qliu $
 */
public class JoinTeamRequest extends BaseRequest {

    /** Handle for the team to join */
    String teamName;

    /**
     * Creates a new instance of <code>JoinTeamRequest</code>. It is required by custom serialization.
     */
    public JoinTeamRequest() {
    }

    /**
     * Constructor for JoinTeamRequest.
     * 
     * @param teamName Handle of the team to join.
     */
    public JoinTeamRequest(String teamName) {
        this.teamName = teamName;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeString(teamName);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        teamName = reader.readString();
    }

    public int getRequestType() {
        return ContestConstants.JOIN_TEAM;
    }

    /**
     * Gets the name of the team to join.
     * 
     * @return Handle of the team to join.
     */
    public String getTeamName() {
        return teamName;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.request.JoinTeamRequest) [");
        ret.append("teamName = ");
        if (teamName == null) {
            ret.append("null");
        } else {
            ret.append(teamName.toString());
        }
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }
}