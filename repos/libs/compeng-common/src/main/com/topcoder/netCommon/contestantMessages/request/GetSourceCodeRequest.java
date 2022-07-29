/*
 * GetSourceCodeRequest Created 06/14/2007
 */
package com.topcoder.netCommon.contestantMessages.request;

import java.io.IOException;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a request to get another coder's solution of a marathon problem.<br>
 * Use: When the current user wants to view another coder's solution of a marathon problem component, this request is
 * sent.<br>
 * Note: When viewing another coder's solution, the phase must be in system testing/end of contest phase. This request
 * <b>does not</b> automatically changes the state of other coding/viewing/challenging window to close. If there is
 * other opening coding/viewing/challenging window, this request will fail. The client has to change the
 * coding/viewing/challenging window to close by sending <code>CloseProblemRequest</code>.
 * 
 * @author Diego Belfer (Mural)
 * @version $Id: GetSourceCodeRequest.java 72163 2008-08-07 07:51:04Z qliu $
 * @see CloseProblemRequest
 */
public class GetSourceCodeRequest extends BaseRequest {
    /** Represents the ID of the round where the requested solution belongs. */
    private int roundId;

    /** Represents the handle of the other coder whose solution is requested. */
    private String handle;

    /** Represents the ID of the problem component where the requested solution belongs. */
    private int componentId;

    /** Represents a flag indicating if example submissions is requested instead of full submissions. */
    private boolean example;

    /** Represents the requested solution's submission number. */
    private int submissionNumber;

    /** Represents a flag indicating if the solution should be reformatted on the server. */
    private boolean pretty;

    /**
     * Creates a new instance of <code>GetSourceCodeRequest</code>. It is required by custom serialization.
     */
    public GetSourceCodeRequest() {
    }

    /**
     * Creates a new instance of <code>GetSourceCodeRequest</code>.
     * 
     * @param roundId the ID of the round where the requested solution belongs.
     * @param handle the handle of the other coder whose solution is requested.
     * @param componentId the ID of the problem component where the requested solution belongs.
     * @param example <code>true</code> if example submissions is requested; <code>false</code> otherwise.
     * @param submissionNumber the requested solution's submission number.
     * @param pretty <code>true</code> if the solution needs to be reformatted on the server; <code>false</code>
     *            otherwise.
     */
    public GetSourceCodeRequest(int roundId, String handle, int componentId, boolean example, int submissionNumber,
        boolean pretty) {
        this.roundId = roundId;
        this.handle = handle;
        this.componentId = componentId;
        this.example = example;
        this.submissionNumber = submissionNumber;
        this.pretty = pretty;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeInt(roundId);
        writer.writeString(handle);
        writer.writeInt(componentId);
        writer.writeBoolean(example);
        writer.writeInt(submissionNumber);
        writer.writeBoolean(pretty);
    }

    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        this.roundId = reader.readInt();
        this.handle = reader.readString();
        this.componentId = reader.readInt();
        this.example = reader.readBoolean();
        this.submissionNumber = reader.readInt();
        this.pretty = reader.readBoolean();
    }

    /**
     * Gets a flag indicating if the solution should be reformatted on the server.
     * 
     * @return <code>true</code> if the solution needs to be reformatted on the server; <code>false</code>
     *         otherwise.
     */
    public boolean isPretty() {
        return pretty;
    }

    public int getRequestType() {
        return ContestConstants.GET_SOURCE_CODE_REQUEST;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.request.GetSourceCodeRequest) [");
        ret.append("handle = ");
        ret.append(handle);
        ret.append(", ");
        ret.append("componentID = ");
        ret.append(componentId);
        ret.append(", ");
        ret.append("pretty = ");
        ret.append(pretty);
        ret.append(", ");
        ret.append("roundId = ");
        ret.append(roundId);
        ret.append(", ");
        ret.append("example = ");
        ret.append(example);
        ret.append(", ");
        ret.append("submission = ");
        ret.append(submissionNumber);
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }

    /**
     * Gets the ID of the problem component where the requested solution belongs.
     * 
     * @return the ID of the problem component.
     */
    public int getComponentId() {
        return componentId;
    }

    /**
     * Gets a flag indicating if example submissions is requested instead of full submissions.
     * 
     * @return <code>true</code> if example submissions is requested; <code>false</code> otherwise.
     */
    public boolean isExample() {
        return example;
    }

    /**
     * Gets the handle of the other coder whose solution is requested.
     * 
     * @return the handle of the other coder.
     */
    public String getHandle() {
        return handle;
    }

    /**
     * Gets the ID of the round where the requested solution belongs.
     * @return the ID of the round.
     */
    public int getRoundId() {
        return roundId;
    }

    /**
     * Gets the requested solution's submission number.
     * @return the requested solution's submission number.
     */
    public int getSubmissionNumber() {
        return submissionNumber;
    }
}
