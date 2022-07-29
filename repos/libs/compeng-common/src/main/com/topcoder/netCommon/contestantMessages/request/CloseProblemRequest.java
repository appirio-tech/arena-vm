package com.topcoder.netCommon.contestantMessages.request;

import java.io.IOException;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a request to notify the server that the current user stops coding or viewing other's code.<br>
 * Use: When a user closes the coding window or closes the challenge window (viewing other's code), this request should
 * be sent.<br>
 * Note: The current user does not need to be in a room when sending this request. However, the current user should open
 * a coding window or view other's code before sending the request.
 * 
 * @author Walter Mundt
 * @version $Id: CloseProblemRequest.java 72163 2008-08-07 07:51:04Z qliu $
 * @see GetChallengeProblemRequest
 * @see GetSourceCodeRequest
 * @see OpenComponentForCodingRequest
 */
public class CloseProblemRequest extends BaseRequest {
    /** Represents the ID of the problem component being closed. */
    private int problemID;

    /**
     * Represents the handle of the coder whose code is being closed. When closing the coding window, the writer is the
     * current user. Otherwise, it is the user whose code is being viewed.
     */
    private String writer;

    public int getRequestType() {
        return ContestConstants.CLOSE_PROBLEM;
    }

    /**
     * Creates a new instance of <code>CloseProblemRequest</code>. It is required by custom serialization.
     */
    public CloseProblemRequest() {
    }

    /**
     * Creates a new instance of <code>CloseProblemRequest</code>.
     * 
     * @param problemID the ID of the problem component.
     * @param writer the writer of the code.
     * @see #getWriter()
     */
    public CloseProblemRequest(int problemID, String writer) {
        this.problemID = problemID;
        this.writer = writer;
    }

    /**
     * Gets the ID of problem component whose code is being closed.
     * 
     * @return the ID of the problem component.
     */
    public int getProblemID() {
        return problemID;
    }

    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        problemID = reader.readInt();
        writer = reader.readString();
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeInt(problemID);
        writer.writeString(this.writer);
    }

    /**
     * Gets the writer's handle whose writes the code. When closing the coding window, the writer is the current user.
     * Otherwise, the writer is the user whose code is being viewed.
     * 
     * @return the handle of the writer.
     */
    public String getWriter() {
        return writer;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.request.CloseProblemRequest) [");
        ret.append("problemID = ");
        ret.append(problemID);
        ret.append(", ");
        ret.append("writer = ");
        if (writer == null) {
            ret.append("null");
        } else {
            ret.append(writer.toString());
        }
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }
}
