package com.topcoder.netCommon.contestantMessages.request;

import com.topcoder.netCommon.contest.ContestConstants;

/**
 * Indicates that the client should receive an UpdateTeamListResponse for each team and should continue to receive
 * updates for each team until a CloseTeamListRequest is sent by the client.
 * 
 * @see com.topcoder.netCommon.contestantMessages.response.UpdateTeamListResponse
 * @see CloseTeamListRequest
 * @author Matthew P. Suhocki (msuhocki)
 * @version $Id: GetTeamListRequest.java 72163 2008-08-07 07:51:04Z qliu $
 */
public class GetTeamListRequest extends BaseRequest {

    public int getRequestType() {
        return ContestConstants.GET_TEAM_LIST;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.request.GetTeamListRequest) [");
        ret.append("]");
        return ret.toString();
    }
}
