package com.topcoder.netCommon.contestantMessages.request;

import com.topcoder.netCommon.contest.ContestConstants;

/**
 * Defines a request to log the current user out.<br>
 * Use: When the current user wants to log out, this request should be sent.<br>
 * Note: The current user should be logged in before this request can be sent.
 * 
 * @author Walter Mundt
 * @version $Id: LogoutRequest.java 72292 2008-08-12 09:10:29Z qliu $
 */
public class LogoutRequest extends BaseRequest {
    public int getRequestType() {
        return ContestConstants.LOGOUT;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.request.LogoutRequest) [");
        ret.append("]");
        return ret.toString();
    }
}
