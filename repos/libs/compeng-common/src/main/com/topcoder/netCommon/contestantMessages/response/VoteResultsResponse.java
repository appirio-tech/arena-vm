package com.topcoder.netCommon.contestantMessages.response;

import java.io.IOException;

import com.topcoder.netCommon.contestantMessages.response.data.VoteResultsCoder;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a response to send the final voting result of a 'Weakest Link' round to the client.<br>
 * Use: This response is sent automatically by the server when ending the 'Weakest Link' round. The voting result
 * determines the elimination of the user. It should be shown to the current user.
 * 
 * @author Qi Liu
 * @version $Id: VoteResultsResponse.java 72385 2008-08-19 07:00:36Z qliu $
 */
public final class VoteResultsResponse extends BaseResponse {
    /** Represents the name of the 'Weakest Link' round. */
    private String roundName;

    /** Represents the voting result for each participant. */
    private VoteResultsCoder[] coders;

    /**
     * Creates a new instance of <code>VoteResultsResponse</code>. It is required by custom serialization.
     */
    public VoteResultsResponse() {
    }

    /**
     * Creates a new instance of <code>VoteResultsResponse</code>. There is no copy.
     * 
     * @param roundName the name of the 'Weakest Link' round.
     * @param coders the voting result for each participant.
     */
    public VoteResultsResponse(String roundName, VoteResultsCoder[] coders) {
        this.roundName = roundName;
        this.coders = coders;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeObjectArray(coders);
        writer.writeString(roundName);
    }

    public void customReadObject(CSReader reader) throws IOException {
        coders = (VoteResultsCoder[]) reader.readObjectArray(VoteResultsCoder.class);
        roundName = reader.readString();
    }

    /**
     * Gets the name of the 'Weakest Link' round.
     * 
     * @return the round name.
     */
    public String getRoundName() {
        return roundName;
    }

    /**
     * Gets the voting result for each participant. There is no copy.
     * 
     * @return the voting result for each participant.
     */
    public VoteResultsCoder[] getCoders() {
        return coders;
    }

}
