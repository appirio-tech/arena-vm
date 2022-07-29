package com.topcoder.netCommon.contestantMessages.response;

import com.topcoder.shared.netCommon.messages.Message;

/**
 * Defines a base abstract class of all responses related to arena communication. Not each request has its specific
 * corresponding response.<br>
 * Note: All messages sent by the server to the client is called a response. If there is any error in the request,
 * usually a popup dialog response is sent.
 * 
 * @author Lars Backstrom
 * @version $Id: BaseResponse.java 72300 2008-08-13 08:33:29Z qliu $
 */
public abstract class BaseResponse extends Message {
    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.response.BaseResponse) [");
        ret.append("]");
        return ret.toString();
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
