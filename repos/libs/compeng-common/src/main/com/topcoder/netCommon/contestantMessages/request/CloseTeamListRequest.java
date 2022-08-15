/*
 * CloseTeamListRequest.java
 *
 * Created on June 28, 2002, 12:45 AM
 */

package com.topcoder.netCommon.contestantMessages.request;

import com.topcoder.netCommon.contest.ContestConstants;

/**
 * Indicates that the client should no longer receive team list updates.
 *
 * @see GetTeamListRequest
 *
 * @author      Matthew P. Suhocki (msuhocki)
 * @version $Id: CloseTeamListRequest.java 72163 2008-08-07 07:51:04Z qliu $
 */
public class CloseTeamListRequest extends BaseRequest {

    public int getRequestType() {
        return ContestConstants.CLOSE_TEAM_LIST;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.request.CloseTeamListRequest) [");
        ret.append("]");
        return ret.toString();
    }
}
