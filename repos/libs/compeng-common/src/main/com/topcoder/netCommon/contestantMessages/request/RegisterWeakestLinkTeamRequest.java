/**
 * RegisterWeakestLinkTeamRequest.java Description: A request by a spectator for regular status updates for a weakest
 * link team
 * 
 * @author Dave Pecora
 * @version 1.0
 */

package com.topcoder.netCommon.contestantMessages.request;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a request to notify the server to start sending all events of a team to the client.<br>
 * Use: This request is used by scoreboard application to render the current status of a 'Weakest Link' contest team.<br>
 * Note: The arena applet should never use this request.
 * 
 * @author Dave Pecora
 * @version $Id: RegisterWeakestLinkTeamRequest.java 72292 2008-08-12 09:10:29Z qliu $
 */
public class RegisterWeakestLinkTeamRequest extends BaseRequest {
    /** Represents the ID of the team to be subscribed. */
    int teamID;

    /**
     * Creates a new instance of <code>RegisterWeakestLinkTeamRequest</code>. It is required by custom serialization.
     */
    public RegisterWeakestLinkTeamRequest() {
    }

    /**
     * Creates a new instance of <code>RegisterWeakestLinkTeamRequest</code>.
     * 
     * @param teamID the ID of the team to be subscribed.
     */
    public RegisterWeakestLinkTeamRequest(int teamID) {
        this.teamID = teamID;
    }

    /**
     * Gets the team ID requested by the spectator.
     * 
     * @return the team ID
     */
    public int getTeamID() {
        return teamID;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeInt(teamID);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        teamID = reader.readInt();
    }

    public String toString() {
        return "(com.topcoder.netCommon.contestantMessages.request.RegisterWeakestLinkTeamRequest) [teamID = " + teamID
            + "]";
    }

    public int getRequestType() {
        return ContestConstants.REGISTER_WEAKEST_LINK_TEAM;
    }
}
