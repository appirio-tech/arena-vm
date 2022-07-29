package com.topcoder.netCommon.contestantMessages.response;

import java.io.IOException;

import com.topcoder.netCommon.contestantMessages.response.data.WLTeamInfo;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a response to send the information of all teams in a 'Weakest Link' round to the client. A prize threshold of
 * the same round is also sent.<br>
 * Use: This response is specific to <code>WLTeamsInfoRequest</code>. The information in this response should be
 * shown to the current user.<br>
 * Note: The ID of the round is not in the response, as it is expected to have at most one active 'Weakest Link' round
 * at any time.
 * 
 * @author Qi Liu
 * @version $Id: WLTeamsInfoResponse.java 72385 2008-08-19 07:00:36Z qliu $
 */
public final class WLTeamsInfoResponse extends BaseResponse {
    /** Represents the information of all teams in the round. */
    private WLTeamInfo[] teams;

    /** Represents the prize threshold of the round. */
    private double prizeThreshold;

    /**
     * Creates a new instance of <code>WLTeamsInfoResponse</code>. It is required by custom serialization.
     */
    public WLTeamsInfoResponse() {
    }

    /**
     * Creates a new instance of <code>WLTeamsInfoResponse</code>. There is no copy.
     * 
     * @param teams the information of all teams in the round.
     * @param prizeThreshold the prize threshold of the round.
     */
    public WLTeamsInfoResponse(WLTeamInfo[] teams, double prizeThreshold) {
        this.teams = teams;
        this.prizeThreshold = prizeThreshold;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeObjectArray(teams);
        writer.writeDouble(prizeThreshold);
    }

    public void customReadObject(CSReader reader) throws IOException {
        teams = (WLTeamInfo[]) reader.readObjectArray(WLTeamInfo.class);
        prizeThreshold = reader.readDouble();
    }

    /**
     * Gets the information of all teams in the round. There is no copy.
     * 
     * @return the information of all teams.
     */
    public WLTeamInfo[] getTeams() {
        return teams;
    }

    /**
     * Gets the prize threshold of the round.
     * 
     * @return the prize threshold.
     */
    public double getPrizeThreshold() {
        return prizeThreshold;
    }

}
