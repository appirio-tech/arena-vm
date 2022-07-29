package com.topcoder.netCommon.contestantMessages.request;

import java.io.IOException;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a request to submit the compiled code by the current user for a problem component.<br>
 * Use: During the coding phase, when the current user wants to submit the code, this request should be sent.<br>
 * Note: The current user must in the room he is assigned, and the current phase must be coding phase. The problem
 * component must be open for coding. The most recent compilation is submitted regardless of the current editing/saved
 * code.
 * 
 * @author Walter Mundt
 * @version $Id: SubmitRequest.java 72292 2008-08-12 09:10:29Z qliu $
 */
public final class SubmitRequest extends BaseRequest {
    /** Represents the ID of the problem component to be submitted. */
    int componentID;

    /**
     * Creates a new instance of <code>SubmitRequest</code>. It is required by custom serialization.
     */
    public SubmitRequest() {
    }

    /**
     * Creates a new instance of <code>SubmitRequest</code>.
     * 
     * @param componentID the ID of the problem component to be submitted.
     */
    public SubmitRequest(int componentID) {
        this.componentID = componentID;
    }

    public int getRequestType() {
        return ContestConstants.SUBMIT_PROBLEM;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeInt(componentID);
    }

    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        componentID = reader.readInt();
    }

    /**
     * Gets the ID of the problem component to be submitted.
     * 
     * @return the problem component ID.
     */
    public int getComponentID() {
        return componentID;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.request.SubmitRequest) [");
        ret.append("componentID = ");
        ret.append(componentID);
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }
}
