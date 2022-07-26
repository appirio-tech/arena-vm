package com.topcoder.netCommon.contestantMessages.request;

import com.topcoder.netCommon.contest.ContestConstants;

/**
 * Defines a request to notify the server to start/stop sending chat messages to the client. There is no payload.<br>
 * Use: When the current user wants to start/stop see the chat in the room, this request is sent.<br>
 * Note: This request acts like a toggle. When the server sends the chat messages, this request stops it; otherwise,
 * this request makes the server starting to send chat messages.
 * 
 * @author Walter Mundt
 * @version $Id: ToggleChatRequest.java 72292 2008-08-12 09:10:29Z qliu $
 */
public class ToggleChatRequest extends BaseRequest {
    public int getRequestType() {
        return ContestConstants.TOGGLE_CHAT;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.request.ToggleChatRequest) [");
        ret.append("]");
        return ret.toString();
    }
}
