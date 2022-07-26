package com.topcoder.netCommon.contestantMessages.request;

import java.io.IOException;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a request to get the statistics of a 'Weakest Link' round.<br>
 * Use: Before voting for the elimination of a member, the statistics of users can be viewed. In this case this request
 * is sent.<br>
 * Note: The round must be a 'Weakest Link' round.
 * 
 * @author Qi Liu
 * @version $Id: RoundStatsRequest.java 72292 2008-08-12 09:10:29Z qliu $
 */
public final class RoundStatsRequest extends BaseRequest {
    /** Represents the ID of the 'Weakest Link' round. */
    private int roundId;

    /** Represents the handle of the member whose statistics are requested. */
    private String coderName;

    /**
     * Creates a new instance of <code>RoundStatsRequest</code>. It is required by custom serialization.
     */
    public RoundStatsRequest() {
    }

    /**
     * Creates a new instance of <code>RoundStatsRequest</code>.
     * 
     * @param roundId the ID of the 'Weakest Link' round.
     * @param coderName the handle of the member whose statistics are requested.
     */
    public RoundStatsRequest(int roundId, String coderName) {
        this.roundId = roundId;
        this.coderName = coderName;
    }

    public int getRequestType() {
        return -1;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(roundId);
        writer.writeString(coderName);
    }

    public void customReadObject(CSReader reader) throws IOException {
        roundId = reader.readInt();
        coderName = reader.readString();
    }

    /**
     * Gets the ID of the 'Weakest Link' round.
     * 
     * @return the round ID.
     */
    public int getRoundId() {
        return roundId;
    }

    /**
     * Gets the handle of the user whose statistics are requested.
     * 
     * @return the handle of the user.
     */
    public String getCoderName() {
        return coderName;
    }

}
