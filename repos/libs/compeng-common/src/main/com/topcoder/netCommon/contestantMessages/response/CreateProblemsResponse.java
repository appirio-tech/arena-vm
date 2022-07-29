package com.topcoder.netCommon.contestantMessages.response;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.netCommon.contestantMessages.response.data.ComponentLabel;
import com.topcoder.netCommon.contestantMessages.response.data.ProblemLabel;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a response to send the information of all problems assigned to a division of a round. If the round is a team
 * round, the assigned problem components to the current user is also sent to the client.<br>
 * Use: When moving to a practice room or loading a contest round, the information of all problems used in that
 * round/room is sent to the client.<br>
 * Note: The client will receive all information of all divisions in the round, regardless the actual division the
 * current user is/will be assigned to.
 * 
 * @author Lars Backstrom
 * @version $Id: CreateProblemsResponse.java 72343 2008-08-15 06:09:22Z qliu $
 */
public class CreateProblemsResponse extends BaseResponse {
    /** Represents the division of the round. */
    private int divisionID;

    /** Represents the ID of the round. */
    private long roundID;

    /** Represents the problems assigned to the division of the round. */
    private ProblemLabel problems[];

    /** Represents the problem components assigned to the current user of the receiver. */
    private ComponentLabel assignedComponents[];

    /**
     * Creates a new instance of <code>CreateProblemsResponse</code>. It is required by custom serialization.
     */
    public CreateProblemsResponse() {
    }

    /**
     * Creates a new instance of <code>CreateProblemsResponse</code>.
     * 
     * @param problems the problems assigned to the division of the round.
     * @param assignedComponents the problem components assigned to the current user of the receiver.
     * @param roundID the ID of the round.
     * @param divisionID the division of the round.
     */
    public CreateProblemsResponse(ProblemLabel[] problems, ComponentLabel[] assignedComponents, long roundID,
        int divisionID) {
        this.roundID = roundID;
        this.assignedComponents = assignedComponents;
        this.problems = problems;
        this.divisionID = divisionID;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeObjectArray(problems);
        writer.writeObjectArray(assignedComponents);
        writer.writeLong(roundID);
        writer.writeInt(divisionID);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        problems = (ProblemLabel[]) reader.readObjectArray(ProblemLabel.class);
        assignedComponents = (ComponentLabel[]) reader.readObjectArray(ComponentLabel.class);
        roundID = reader.readLong();
        divisionID = reader.readInt();
    }

    /**
     * Gets the problem components assigned to the current user of the receiver.
     * 
     * @return the problem components assigned to the current user.
     */
    public ComponentLabel[] getAssignedComponents() {
        return assignedComponents;
    }

    /**
     * Gets the problems assigned to the division of the round.
     * @return the problems assigned to the division of round.
     */
    public ProblemLabel[] getProblems() {
        return problems;
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
        ret.append("(com.topcoder.netCommon.contestantMessages.response.CreateProblemsResponse) [");
        ret.append("problems = ");
        if (problems == null) {
            ret.append("null");
        } else {
            ret.append(problems.toString());
        }
        ret.append(", ");
        ret.append("roundID = " + roundID);
        ret.append(", ");
        ret.append("divisionID = " + divisionID);
        ret.append("]");
        return ret.toString();
    }
}
