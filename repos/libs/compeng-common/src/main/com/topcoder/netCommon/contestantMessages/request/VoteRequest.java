package com.topcoder.netCommon.contestantMessages.request;

import java.io.IOException;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a request to vote for the user to be eliminated in a 'Weakest Link' round.<br>
 * Use: During a 'Weakest Link' round, a voted user is eliminated each time. In this case, the voting is done via this
 * request.<br>
 * Note: The round must be a 'Weakest Link' round, and the current user must be a participants of the round.
 * 
 * @author Qi Liu
 * @version $Id: VoteRequest.java 72292 2008-08-12 09:10:29Z qliu $
 */
public final class VoteRequest extends BaseRequest {
    /** Represents the ID of the 'Weakest Link' round. */
    private int roundId;

    /** Represents the handle of the user to be voted. */
    private String coderName;

    /**
     * Creates a new instance of <code>VoteRequest</code>. It is required by custom serialization.
     */
    public VoteRequest() {
    }

    /**
     * Creates a new instance of <code>VoteRequest</code>.
     * 
     * @param roundId the ID of the 'Weakest Link' round.
     * @param coderName the handle of the user to be voted.
     */
    public VoteRequest(int roundId, String coderName) {
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
     * Gets the handle of the user to be voted.
     * 
     * @return the handle of the user.
     */
    public String getCoderName() {
        return coderName;
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
