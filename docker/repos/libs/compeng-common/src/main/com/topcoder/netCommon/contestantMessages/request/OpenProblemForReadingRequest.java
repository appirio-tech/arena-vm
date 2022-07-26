package com.topcoder.netCommon.contestantMessages.request;

import java.io.IOException;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a request to get the problem statement of a marathon/team problem component.<br>
 * Use: In marathon and team competition, the user is allowed to read the problem statement before
 * registration/assignment. In this case, this request is sent.<br>
 * Note: The viewing can only happen if it is coding/challenging/intermission/system testing/contest complete phase.
 * When the problem component is a team problem component, the current user must be in the assigned room. The handle of
 * the current user is ignored.
 * 
 * @author Walter Mundt
 * @version $Id: OpenProblemForReadingRequest.java 72292 2008-08-12 09:10:29Z qliu $
 */
public class OpenProblemForReadingRequest extends BaseRequest {
    /** Represents the ID of the round where the problem component is assigned. */
    protected int roundId;

    /** Represents the ID of the problem component. */
    protected int problemID;

    /** Represents the handle of the current user. It is ignored. */
    protected String handle;

    /**
     * Creates a new instance of <code>OpenProblemForReadingRequest</code>. It is required by custom serialization.
     */
    public OpenProblemForReadingRequest() {
    }

    /**
     * Creates a new instance of <code>OpenProblemForReadingRequest</code>. The handle of the current user is unset
     * and ignored.
     * 
     * @param roundId the ID of the round where the problem component is assigned.
     * @param problemID the ID of the problem component.
     */
    public OpenProblemForReadingRequest(int roundId, int problemID) {
        this.roundId = roundId;
        this.problemID = problemID;
    }

    /**
     * Creates a new instance of <code>OpenProblemForReadingRequest</code>. The handle of the current user is
     * ignored.
     * 
     * @param handle the handle of the current user.
     * @param roundId the ID of the round where the problem component is assigned.
     * @param problemID the ID of the problem component.
     */
    public OpenProblemForReadingRequest(String handle, int roundId, int problemID) {
        this(roundId, problemID);
        this.handle = handle;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeInt(roundId);
        writer.writeInt(problemID);
        writer.writeString(handle);
    }

    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        roundId = reader.readInt();
        problemID = reader.readInt();
        handle = reader.readString();
    }

    public int getRequestType() {
        return ContestConstants.GET_PROBLEM;
    }

    /**
     * Gets the ID of the problem component.
     * 
     * @return the problem component ID.
     */
    public int getProblemID() {
        return problemID;
    }

    /**
     * Gets the handle of the current user.
     * 
     * @return the handle of the current user.
     */
    public String getHandle() {
        return handle;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.request.OpenProblemForReadingRequest) [");
        ret.append("problemID = ");
        ret.append(problemID);
        ret.append(", ");
        ret.append("handle = ");
        ret.append(handle);
        ret.append("]");
        return ret.toString();
    }

    /**
     * Gets the ID of the round where the problem component is assigned.
     * 
     * @return the round ID.
     */
    public int getRoundId() {
        return roundId;
    }
}
