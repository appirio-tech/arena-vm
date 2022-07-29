/*
 * ViewQueueRequest Created 06/19/2007
 */
package com.topcoder.netCommon.contestantMessages.request;

import com.topcoder.netCommon.contest.ContestConstants;

/**
 * Defines a request to get the testing queue for marathon tests. There is no payload.<br>
 * Use: The current user may use this request to see the current testing queue status, since marathon tests
 * are usually long.
 * 
 * @author Diego Belfer (Mural)
 * @version $Id: ViewQueueRequest.java 72292 2008-08-12 09:10:29Z qliu $
 */
public class ViewQueueRequest extends BaseRequest {
    public int getRequestType() {
        return ContestConstants.VIEW_QUEUE_REQUEST;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.request.ViewQueueRequest) [");
        ret.append("]");
        return ret.toString();
    }
}
