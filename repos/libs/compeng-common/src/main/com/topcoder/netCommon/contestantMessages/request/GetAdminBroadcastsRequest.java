package com.topcoder.netCommon.contestantMessages.request;

import com.topcoder.netCommon.contest.ContestConstants;

/**
 * Defines a request to get all admin broadcasts. It has no payload.<br>
 * Use: When the current user wants to review all admin broadcasts, this request is sent to get a list of all
 * broadcasts.
 * 
 * @author Walter Mundt
 * @version $Id: GetAdminBroadcastsRequest.java 72163 2008-08-07 07:51:04Z qliu $
 */
public class GetAdminBroadcastsRequest extends BaseRequest {
    public int getRequestType() {
        return ContestConstants.GET_ADMIN_BROADCAST;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.request.GetAdminBroadcastsRequest) [");
        ret.append("]");
        return ret.toString();
    }
}
