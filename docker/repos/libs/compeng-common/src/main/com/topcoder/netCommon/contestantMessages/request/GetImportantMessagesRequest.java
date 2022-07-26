package com.topcoder.netCommon.contestantMessages.request;

import java.io.IOException;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a request to get all important messages. It has no payload.<br>
 * Use: When the current user wants to view all important messages, such as changes of Java version in the testing
 * machines, this request is sent.
 * 
 * @author Ryan Fairfax
 * @version $Id: GetImportantMessagesRequest.java 72163 2008-08-07 07:51:04Z qliu $
 */
public class GetImportantMessagesRequest extends BaseRequest {
    /**
     * Creates a new instance of <code>GetImportantMessagesRequest</code>. It is required by custom serialization.
     */
    public GetImportantMessagesRequest() {
    }

    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
    }

    public int getRequestType() {
        return ContestConstants.GET_IMPORTANT_MESSAGES_REQUEST;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.request.GetImportantMessagesRequest) [");
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }
}
