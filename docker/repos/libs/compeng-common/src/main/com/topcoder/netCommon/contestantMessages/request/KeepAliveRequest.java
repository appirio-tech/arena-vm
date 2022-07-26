package com.topcoder.netCommon.contestantMessages.request;

import java.io.IOException;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a request to prevent long-time communication idling which causes time outs. There is no payload.<br>
 * Use: This request should be sent automatically when there is no communication for certain time. If there is constant
 * communication between server and client, this request should not be sent in order to reduce the network demands, etc.
 * This request can be sent before login.
 * 
 * @author LarsBackstrom
 * @version $Id: KeepAliveRequest.java 72292 2008-08-12 09:10:29Z qliu $
 */
public class KeepAliveRequest extends BaseRequest {
    /**
     * Creates a new instance of <code>KeepAliveRequest</code>. It is required by custom serialization.
     */
    public KeepAliveRequest() {
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
    }

    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
    }

    public int getRequestType() {
        return ContestConstants.KEEP_ALIVE_REQUEST;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.request.KeepAliveRequest) [");
        ret.append("]");
        return ret.toString();
    }
}
