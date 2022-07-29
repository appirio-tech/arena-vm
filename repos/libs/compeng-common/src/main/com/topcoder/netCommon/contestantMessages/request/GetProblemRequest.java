package com.topcoder.netCommon.contestantMessages.request;

import java.io.IOException;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a request to get the problem statement of a problem component. It is not supported now.
 * 
 * @author Walter Mundt
 * @version $Id: GetProblemRequest.java 72163 2008-08-07 07:51:04Z qliu $
 */
public class GetProblemRequest extends BaseRequest {
    /** Represents the ID of the problem component. */
    protected int problemID;

    /**
     * Creates a new instance of <code>GetProblemRequest</code>. It is required by custom serialization.
     */
    public GetProblemRequest() {
    }

    /**
     * Creates a new instance of <code>GetProblemRequest</code>.
     * 
     * @param problemID the ID of the problem component whose problem statement is requested.
     */
    public GetProblemRequest(int problemID) {
        this.problemID = problemID;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeInt(problemID);
    }

    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        problemID = reader.readInt();
    }

    public int getRequestType() {
        return ContestConstants.GET_PROBLEM;
    }

    /**
     * Gets the ID of the problem component whose problem statement is requested.
     * 
     * @return the ID of the problem component.
     */
    public int getProblemID() {
        return problemID;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.request.GetProblemRequest) [");
        ret.append("problemID = ");
        ret.append(problemID);
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }
}
