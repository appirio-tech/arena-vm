package com.topcoder.netCommon.contestantMessages.request;

import java.io.IOException;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a request to get the list of all logged in users. There is no payload.<br>
 * Use: The request is sent by the client when the user requests to see the active user list.
 * 
 * @author Walter Mundt
 * @version $Id: ActiveUsersRequest.java 72143 2008-08-06 05:54:59Z qliu $
 */
public class ActiveUsersRequest extends BaseRequest {
    /**
     * Creates a new instance of <code>ActiveUsersRequest</code>.
     */
    public ActiveUsersRequest() {
    }

    public int getRequestType() {
        return ContestConstants.LOGGED_IN_USERS;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
    }

    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.request.ActiveUsersRequest) [");
        ret.append("]");
        return ret.toString();
    }
}
