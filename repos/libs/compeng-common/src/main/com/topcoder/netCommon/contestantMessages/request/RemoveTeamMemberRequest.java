package com.topcoder.netCommon.contestantMessages.request;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Requests the specified user be removed from the contestant's (must be captain) team. The specified user must be on
 * the roster of available members and the roster of assigned members on the captain's team.<br>
 * Use: In a team round, the team leader can remove a user from the team member. In this case, this request is sent.<br>
 * Note: The user should be in the team of the current user, and the current user must be the team leader.
 * 
 * @author Matthew P. Suhocki
 * @version $Id: RemoveTeamMemberRequest.java 72292 2008-08-12 09:10:29Z qliu $
 */
public class RemoveTeamMemberRequest extends BaseRequest {
    /** Handle of the user to remove */
    String userHandle;

    /**
     * Creates a new instance of <code>RemoveTeamMemberRequest</code>. It is required by custom serialization.
     */
    public RemoveTeamMemberRequest() {
    }

    /**
     * Constructor for RemoveTeamMemberRequest
     * 
     * @param userHandle Handle of the user to remove
     */
    public RemoveTeamMemberRequest(String userHandle) {
        this.userHandle = userHandle;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeString(userHandle);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        userHandle = reader.readString();
    }

    /**
     * Gets the handle of the user to be removed from the team member.
     * 
     * @return handle of the user to be removed.
     */
    public String getUserHandle() {
        return userHandle;
    }

    public int getRequestType() {
        return ContestConstants.ADD_TEAM_MEMBER;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.request.RemoveTeamMemberRequest) [");
        ret.append("userHandle = ");
        if (userHandle == null) {
            ret.append("null");
        } else {
            ret.append(userHandle.toString());
        }
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }
}
