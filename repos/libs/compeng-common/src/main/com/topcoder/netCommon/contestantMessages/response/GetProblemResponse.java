package com.topcoder.netCommon.contestantMessages.response;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.client.render.ProblemRenderer;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.problem.Problem;

/**
 * Defines a response to send a problem (including its problem components) of a division in a round to the client.<br>
 * Use: This response is used as the response of <code>GetProblemRequest</code> and part of the responses of
 * <code>GetChallengeProblemRequest</code>. Once the client receives this response, the problem and its problem
 * components can be rendered to the current user using <code>ProblemRenderer</code>.
 * 
 * @author Lars Backstrom
 * @version $Id: GetProblemResponse.java 72343 2008-08-15 06:09:22Z qliu $
 * @see Problem
 * @see ProblemRenderer
 */
public class GetProblemResponse extends BaseResponse {
    /** Represents the ID of the round. */
    private long roundID;

    /** Represents the division of the round. */
    private int divisionID;

    /** Represents the type of the operations available to the source code of the problem. */
    private int type;

    /** Represents the problem, including its problem components. */
    private Problem problem;

    /**
     * Creates a new instance of <code>GetProblemResponse</code>. It is required by custom serialization.
     */
    public GetProblemResponse() {
    }

    /**
     * Creates a new instance of <code>GetProblemResponse</code>. The type can be either
     * <code>ContestConstants.EDIT_SOURCE_RW</code> (for coding) or <code>ContestConstants.VIEW_SOURCE</code> (for
     * challenging).
     * 
     * @param type the type of the operations available to the source code of the problem.
     * @param problem the problem, including its problem components.
     * @param roundID the ID of the round.
     * @param division the division of the round.
     */
    public GetProblemResponse(int type, Problem problem, long roundID, int division) {
        this.problem = problem;
        this.type = type;
        this.roundID = roundID;
        this.divisionID = division;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeInt(type);
        writer.writeObject(problem);
        writer.writeLong(roundID);
        writer.writeInt(divisionID);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        type = reader.readInt();
        problem = (Problem) reader.readObject();
        roundID = reader.readLong();
        divisionID = reader.readInt();
    }

    /**
     * Gets the problem including its problem component.
     * 
     * @return the problem.
     */
    public Problem getProblem() {
        return problem;
    }

    /**
     * Gets the type of the operations available to the source code of the problem. The type can be either
     * <code>ContestConstants.EDIT_SOURCE_RW</code> (for coding) or <code>ContestConstants.VIEW_SOURCE</code> (for
     * challenging).
     * 
     * @return the type of the operations available to the source code of the problem.
     */
    public int getProblemType() {
        return type;
    }

    /**
     * Gets the ID of the round.
     * 
     * @return the round ID.
     */
    public long getRoundID() {
        return roundID;
    }

    /**
     * Gets the division of the round.
     * 
     * @return the division of the round.
     */
    public int getDivisionID() {
        return divisionID;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.response.GetProblemResponse) [");
        ret.append("type = ");
        ret.append(type);
        ret.append(", ");
        ret.append("problem = ");
        if (problem == null) {
            ret.append("null");
        } else {
            ret.append(problem.toString());
        }
        ret.append(", ");
        ret.append("roundID = ");
        ret.append(roundID);
        ret.append(", ");
        ret.append("divisionID = ");
        ret.append(divisionID);
        ret.append("]");
        return ret.toString();
    }
}