package com.topcoder.netCommon.contestantMessages.request;

import java.io.IOException;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a request to get the information of the current user's team in a 'Weakest Link' round.<br>
 * Use: The user may see the information of his team during a 'Weakest Link' round.<br>
 * Note: The round must be a 'Weakest Link' round, and the current user must be a participant.
 * 
 * @author Qi Liu
 * @version $Id: WLMyTeamInfoRequest.java 72292 2008-08-12 09:10:29Z qliu $
 */
public final class WLMyTeamInfoRequest extends BaseRequest {
    /** Represents the ID of the 'Weakest Link' round. */
    private int roundId;

    /**
     * Creates a new instance of <code>WLMyTeamInfoRequest</code>. It is required by custom serialization.
     */
    public WLMyTeamInfoRequest() {
    }

    /**
     * Creates a new instance of <code>WLMyTeamInfoRequest</code>.
     * 
     * @param roundId the ID of the 'Weakest Link' round.
     */
    public WLMyTeamInfoRequest(int roundId) {
        this.roundId = roundId;
    }

    public int getRequestType() {
        return -1;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(roundId);
    }

    public void customReadObject(CSReader reader) throws IOException {
        roundId = reader.readInt();
    }

    /**
     * Gets the ID of the 'Weakest Link' round.
     * 
     * @return the round ID.
     */
    public int getRoundId() {
        return roundId;
    }

}
