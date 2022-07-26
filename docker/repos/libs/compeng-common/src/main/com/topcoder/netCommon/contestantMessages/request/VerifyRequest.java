package com.topcoder.netCommon.contestantMessages.request;

import com.topcoder.netCommon.contest.ContestConstants;

/**
 * Defines a request to notify the server to send the verification class byte code to the client. There is no payload.<br>
 * Use: When the keys are exchanged, before logging in, the client needs to be verified if it is signed by TopCoder. To
 * initiate the verification process, this request is sent.<br>
 * Note: Customized clients can ignore this request, since it is unlike that the file is signed by TopCoder.
 * 
 * @author Qi Liu
 * @version $Id: VerifyRequest.java 72292 2008-08-12 09:10:29Z qliu $
 */
public class VerifyRequest extends BaseRequest {
    /**
     * Creates a new instance of <code>VerifyRequest</code>. It is required by custom serialization.
     */
    public VerifyRequest() {
    }

    public int getRequestType() {
        return ContestConstants.VERIFY_REQUEST;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.request.VerifyRequest) [");
        ret.append("]");
        return ret.toString();
    }
}
