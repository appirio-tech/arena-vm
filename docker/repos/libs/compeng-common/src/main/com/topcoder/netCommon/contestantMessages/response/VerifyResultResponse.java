package com.topcoder.netCommon.contestantMessages.response;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a response to notify the client about the TopCoder signature verification result.<br>
 * Use: This response is specific to <code>VerifyResultRequest</code>. The flag in this response indicates if the
 * signature verification is successful or not.<br>
 * Note: For non-TC signed clients, this response can be ignored.
 * 
 * @author Qi Liu
 * @version $Id: VerifyResultResponse.java 72385 2008-08-19 07:00:36Z qliu $
 */
public class VerifyResultResponse extends BaseResponse {
    /** Represents a flag indicating if the client passes the TopCoder signature verification. */
    private boolean success;

    /**
     * Creates a new instance of <code>VerifyResultResponse</code>. It is required by custom serialization.
     */
    public VerifyResultResponse() {
    }

    /**
     * Creates a new instance of <code>VerifyResultResponse</code>.
     * 
     * @param success <code>true</code> if the client passes the TopCoder signature verification; <code>false</code>
     *            otherwise.
     */
    public VerifyResultResponse(boolean success) {
        super();
        this.success = success;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeBoolean(success);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        success = reader.readBoolean();
    }

    /**
     * Gets a flag indicating if the client passes the TopCoder signature verification.
     * 
     * @return <code>true</code> if the client passes the TopCoder signature verification; <code>false</code>
     *         otherwise.
     */
    public boolean getSuccess() {
        return success;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.response.VerifyResultResponse) [success = ");
        ret.append(success);
        ret.append("]");
        return ret.toString();
    }
}
