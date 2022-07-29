package com.topcoder.netCommon.contestantMessages.request;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Requests that the contestant be removed from the specified team's available roster.<br>
 * Use: When the current user wants to leave the team, this request is sent.
 * 
 * @author Matthew P. Suhocki (msuhocki)
 * @version $Id: LeaveTeamRequest.java 72292 2008-08-12 09:10:29Z qliu $
 */
public class LeaveTeamRequest extends BaseRequest {
    /** handle of the team to leave */
    String teamName = null;

    /**
     * Creates a new instance of <code>LeaveTeamRequest</code>. It is required by custom serialization.
     */
    public LeaveTeamRequest() {
    }

    /**
     * Constructor for LeaveTeamRequest.
     * 
     * @param teamName handle of the team to leave
     */
    public LeaveTeamRequest(String teamName) {
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
        return ContestConstants.LEAVE_TEAM;
    }

    /**
     * Gets the name of the team to leave.
     * 
     * @return handle of the team to leave
     */
    public String getTeamName() {
        return teamName;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.request.LeaveTeamRequest) [");
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
