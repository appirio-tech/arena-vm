package com.topcoder.netCommon.contestantMessages.request;

import java.io.IOException;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a request to notify the server to start sending leader board updates to the client. There is no payload.<br>
 * Use: When the current user opens a leader board window, this request should be sent.<br>
 * Note: The current user does not need to be in a contest room. It should be sent when the leader board is not
 * subscribed. This message is deprecated.
 * 
 * @author Walter Mundt
 * @version $Id: GetLeaderBoardRequest.java 72833 2008-09-17 07:33:19Z qliu $
 */
public class GetLeaderBoardRequest extends BaseRequest {
    /**
     * Creates a new instance of <code>GetLeaderBoardRequest</code>. It is required by custom serialization.
     */
    public GetLeaderBoardRequest() {
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
    }

    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
    }

    public int getRequestType() {
        return ContestConstants.GET_LEADER_BOARD;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.request.GetLeaderBoardRequest) [");
        ret.append("]");
        return ret.toString();
    }
}
