package com.topcoder.netCommon.contestantMessages.request;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Requests the specified user be added to the contestant's (must be captain) team. The specified user must be on the
 * roster of available members on the captain's team.<br>
 * Use: This request is sent by client when the current user (must be a team leader) adds an other user to his team.<br>
 * Note: This request is only available to team rounds.
 * 
 * @author Matthew P. Suhocki
 * @version $Id: AddTeamMemberRequest.java 72143 2008-08-06 05:54:59Z qliu $
 */
public class AddTeamMemberRequest extends BaseRequest {
    /** Handle of the user to add */
    String userHandle;

    /**
     * Creates a new instance of <code>AddTeamMemberRequest</code>. It is required by custom serialization.
     */
    public AddTeamMemberRequest() {
    }

    /**
     * Creates a new instance of <code>AddTeamMemberRequest</code>. The handle of the user to be added is given.
     * 
     * @param userHandle the handle of the user to be added.
     */
    public AddTeamMemberRequest(String userHandle) {
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
     * Gets the handle of the user to be added.
     * 
     * @return the handle of the user to be added.
     */
    public String getUserHandle() {
        return userHandle;
    }

    public int getRequestType() {
        return ContestConstants.ADD_TEAM_MEMBER;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.request.AddTeamMemberRequest) [");
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
