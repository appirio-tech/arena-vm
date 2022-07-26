package com.topcoder.netCommon.contestantMessages.response;

import java.io.IOException;
import java.util.Arrays;

import com.topcoder.netCommon.contestantMessages.response.data.RoundStatsProblem;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a response to send the statistics of a 'Weakest Link' round to the client.<br>
 * Use: This response is specific to <code>RoundStatsRequest</code>. The statistics of the requested user are sent.<br>
 * 
 * @author Qi Liu
 * @version $Id: RoundStatsResponse.java 72343 2008-08-15 06:09:22Z qliu $
 */
public final class RoundStatsResponse extends BaseResponse {
    /** Represents the ID of the 'Weakest Link' round. */
    private int roundId;

    /** Represents the name of the 'Weakest Link' round. */
    private String roundName;

    /** Represents the handle of the user whose statistics is returned. */
    private String coderName;

    /** Represents the statistics of all problem components attempted by the user. */
    private RoundStatsProblem[] problems;

    /**
     * Creates a new instance of <code>RoundStatsResponse</code>. It is required by custom serialization.
     */
    public RoundStatsResponse() {
    }

    /**
     * Creates a new instance of <code>RoundStatsResponse</code>. There is no copy.
     * 
     * @param roundId the ID of the 'Weakest Link' round.
     * @param roundName the name of the 'Weakest Link' round.
     * @param coderName the handle of the user whose statistics is returned.
     * @param problems the statistics of all problem components attempted by the user.
     */
    public RoundStatsResponse(int roundId, String roundName, String coderName, RoundStatsProblem[] problems) {
        this.roundId = roundId;
        this.roundName = roundName;
        this.coderName = coderName;
        this.problems = problems;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(roundId);
        writer.writeString(roundName);
        writer.writeString(coderName);
        writer.writeObjectArray(problems);
    }

    public void customReadObject(CSReader reader) throws IOException {
        roundId = reader.readInt();
        roundName = reader.readString();
        coderName = reader.readString();
        problems = (RoundStatsProblem[]) reader.readObjectArray(RoundStatsProblem.class);
    }

    /**
     * Gets the name of the 'Weakest Link' round.
     * 
     * @return the name of the round.
     */
    public String getRoundName() {
        return roundName;
    }

    /**
     * Gets the handle of the user whose statistics is returned.
     * 
     * @return the handle of the user.
     */
    public String getCoderName() {
        return coderName;
    }

    /**
     * Gets the statistics of all problem components attempted by the user.
     * 
     * @return the statistics.
     */
    public RoundStatsProblem[] getProblems() {
        return problems;
    }

    /**
     * Gets the ID of the 'Weakest Link' round.
     * 
     * @return the round ID.
     */
    public int getRoundId() {
        return roundId;
    }

    public String toString() {
        return "roundId=" + roundId + ", roundName=" + roundName + ", coderName=" + coderName + ", problems="
            + Arrays.asList(problems);
    }

}
