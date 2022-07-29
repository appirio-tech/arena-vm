package com.topcoder.netCommon.contestantMessages.request;

import com.topcoder.shared.netCommon.messages.Message;

/**
 * Defines a base abstract class of all requests related to arena communication. Unless specified explicitly, all
 * requests should be sent after logging in.<br>
 * Note: All messages sent by the client to the server is called a request.
 * 
 * @author Walter Mundt
 * @version $Id: BaseRequest.java 72300 2008-08-13 08:33:29Z qliu $
 */
public abstract class BaseRequest extends Message {
    /**
     * Gets the type of the request. This is used to recognize synchronized requests/responses.
     * 
     * @return the type of the request.
     */
    public abstract int getRequestType();

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.request.BaseRequest) [");
        ret.append("]");
        return ret.toString();
    }

}
