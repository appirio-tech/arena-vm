package com.topcoder.netCommon.contestantMessages.request;

import java.io.IOException;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a request to notify the server about the execution result of the verification class byte code.<br>
 * Use: After received <code>VerifyResponse</code> containing the verification class byte code, the verification class
 * byte code should be executed, and an integer result is obtained. Such integer result should be sent to server using
 * this request.<br>
 * Note: This request should be sent after receiving <code>VerifyResponse</code>. If the client does not send
 * <code>VerifyRequest</code>, this request should never be sent.
 * 
 * @author Qi Liu
 * @version $Id: VerifyResultRequest.java 72292 2008-08-12 09:10:29Z qliu $
 * @see com.topcoder.netCommon.contestantMessages.response.VerifyResponse
 */
public class VerifyResultRequest extends BaseRequest {
    /** Represents the execution result of the verification. */
    private int verification;

    /**
     * Creates a new instance of <code>VerifyResultRequest</code>. It is required by custom serialization.
     */
    public VerifyResultRequest() {
    }

    /**
     * Creates a new instance of <code>VerifyResultRequest</code>.
     * 
     * @param verification the execution result of the verification class.
     */
    public VerifyResultRequest(int verification) {
        this.verification = verification;
    }

    /**
     * Gets the execution result of the verification class.
     * 
     * @return the execution result of the verification class.
     */
    public int getVerification() {
        return verification;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeInt(verification);
    }

    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        verification = reader.readInt();
    }

    public int getRequestType() {
        return ContestConstants.VERIFY_RESULT_REQUEST;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.request.VerifyResultRequest) [");
        ret.append(verification);
        ret.append("]");
        return ret.toString();
    }
}
