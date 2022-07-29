package com.topcoder.netCommon.contestantMessages.request;

import java.io.IOException;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a request to notify the server to stop sending leader board updates to the client. There is no payload.<br>
 * Use: When the current user closes a leader board window, this request should be sent.<br>
 * Note: The current user does not need to be in a contest room. It should be sent when the leader board is subscribed.
 * This message is deprecated.
 * 
 * @author Walter Mundt
 * @version $Id: CloseLeaderBoardRequest.java 72833 2008-09-17 07:33:19Z qliu $
 */
public class CloseLeaderBoardRequest extends BaseRequest {
    /**
     * Creates a new instance of <code>CloseLeaderBoardRequest</code>. It is required by custom serialization.
     */
    public CloseLeaderBoardRequest() {
    }

    public int getRequestType() {
        return ContestConstants.CLOSE_LEADER_BOARD;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
    }

    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.request.CloseLeaderBoardRequest) [");
        ret.append("]");
        return ret.toString();
    }
}
