package com.topcoder.netCommon.contestantMessages.response;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.netCommon.contestantMessages.UserInfo;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a response to send the information of the current user to the client.<br>
 * Use: This response is sent as part of responses of <code>LoginRequest</code> or as part of responses of
 * <code>MoveRequest</code> when moving to a team contest room. It contains the information of the current user.<br>
 * Note: This response is sent as part of responses of <code>LoginRequest</code> only if the login process is
 * successful.
 * 
 * @author Lars Backstrom
 * @version $Id: UserInfoResponse.java 72385 2008-08-19 07:00:36Z qliu $
 */
public class UserInfoResponse extends BaseResponse {
    /** Represents the information of the current user. */
    private UserInfo userInfo;

    /**
     * Creates a new instance of <code>UserInfoResponse</code>. It is required by custom serialization.
     */
    public UserInfoResponse() {
    }

    /**
     * Creates a new instance of <code>UserInfoResponse</code>.
     * 
     * @param userInfo the information of the current user.
     */
    public UserInfoResponse(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeObject(userInfo);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        userInfo = (UserInfo) reader.readObject();
    }

    /**
     * Gets the information of the current user.
     * 
     * @return the information of the current user.
     */
    public UserInfo getUserInfo() {
        return userInfo;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.response.UserInfoResponse) [");
        ret.append("userInfo = ");
        if (userInfo == null) {
            ret.append("null");
        } else {
            ret.append(userInfo.toString());
        }
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }
}
