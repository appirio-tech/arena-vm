package com.topcoder.netCommon.contestantMessages.request;

import java.io.IOException;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a request to get the information of the all teams in a 'Weakest Link' round.<br>
 * Use: The user may see the information of all teams during a 'Weakest Link' round.<br>
 * Note: The round must be a 'Weakest Link' round.
 * 
 * @author Qi Liu
 * @version $Id: WLTeamsInfoRequest.java 72292 2008-08-12 09:10:29Z qliu $
 */
public final class WLTeamsInfoRequest extends BaseRequest {
    /** Represents the ID of the 'Weakest Link' round. */
    private int roundId;

    /**
     * Creates a new instance of <code>WLTeamsInfoRequest</code>. It is required by custom serialization.
     */
    public WLTeamsInfoRequest() {
    }

    /**
     * Creates a new instance of <code>WLTeamsInfoRequest</code>.
     * 
     * @param roundId the ID of the 'Weakest Link' round.
     */
    public WLTeamsInfoRequest(int roundId) {
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
