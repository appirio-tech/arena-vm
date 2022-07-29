package com.topcoder.netCommon.contestantMessages.request;

import com.topcoder.netCommon.contest.ContestConstants;

/**
 * Defines a request to notify the server that there is an error. It has no payload, and is deprecated.
 * 
 * @author Walter Mundt
 * @version $Id: ErrorRequest.java 72163 2008-08-07 07:51:04Z qliu $
 */
public class ErrorRequest extends BaseRequest {
    public int getRequestType() {
        return ContestConstants.ERROR;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.request.ErrorRequest) [");
        ret.append("]");
        return ret.toString();
    }
}
