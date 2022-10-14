package com.topcoder.netCommon.contestantMessages.response;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.netCommon.contestantMessages.response.data.TeamListInfo;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a response to update the information of one team in the team list.<br>
 * Use: When there is a change to a team, such as adding or removing a member from the team, this response is sent to
 * interested clients.<br>
 * Note: This response will be sent only for team rounds. The team will always be in the team list sent by
 * <code>CreateTeamListResponse</code>.
 * 
 * @author Matthew P. Suhocki (msuhocki)
 * @version $Id: UpdateTeamListResponse.java 72385 2008-08-19 07:00:36Z qliu $
 * @see CreateTeamListResponse
 */
public class UpdateTeamListResponse extends BaseResponse {
    /** Represents the updated information of the team. */
    private TeamListInfo info = null;

    /**
     * Creates a new instance of <code>UpdateTeamListResponse</code>. It is required by custom serialization.
     */
    public UpdateTeamListResponse() {
    }

    /**
     * Creates a new instance of <code>UpdateTeamListResponse</code>.
     * 
     * @param info the updated information of the team.
     */
    public UpdateTeamListResponse(TeamListInfo info) {
        this.info = info;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeObject(info);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        info = (TeamListInfo) reader.readObject();
    }

    /**
     * Gets the updated information of the team.
     * 
     * @return the information of the team.
     */
    public TeamListInfo getTeamListInfo() {
        return info;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.response.UpdateTeamListResponse) [");
        ret.append("info = ");
        if (info == null) {
            ret.append("null");
        } else {
            ret.append(info.toString());
        }
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }

}
