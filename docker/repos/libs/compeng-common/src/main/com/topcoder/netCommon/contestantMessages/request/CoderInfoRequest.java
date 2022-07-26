package com.topcoder.netCommon.contestantMessages.request;

import java.io.IOException;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a request to get the basic profile of a coder.<br>
 * Use: When the current user wants to see the profile of a user in the arena, this request should be sent.<br>
 * Note: The queried user does not need to be logged in.
 * 
 * @author Walter Mundt
 * @version $Id: CoderInfoRequest.java 72163 2008-08-07 07:51:04Z qliu $
 */
public class CoderInfoRequest extends BaseRequest {
    /** Represents the handle of the user whose profile is requested. */
    protected String coder;

    /** Represents the type of the user whose profile is requested. */
    protected int userType;

    /**
     * Creates a new instance of <code>CoderInfoRequest</code>. It is required by custom serialization.
     */
    public CoderInfoRequest() {
    }

    /**
     * Creates a new instance of <code>CoderInfoRequest</code>.
     * 
     * @param coder the handle of the user whose profile is requested.
     * @param userType the type of the user whose profile is requested.
     */
    public CoderInfoRequest(String coder, int userType) {
        this.coder = coder;
        this.userType = userType;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeString(coder);
        writer.writeInt(userType);
    }

    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        coder = reader.readString();
        userType = reader.readInt();
    }

    public int getRequestType() {
        return ContestConstants.CODER_INFO;
    }

    /**
     * Gets the handle of the user whose profile is requested.
     * 
     * @return the handle of the user.
     */
    public String getCoder() {
        return coder;
    }

    /**
     * Gets the type of the user whose profile is requested.
     * 
     * @return the type of the user.
     */
    public int getUserType() {
        return userType;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.request.CoderInfoRequest) [");
        ret.append("coder = ");
        if (coder == null) {
            ret.append("null");
        } else {
            ret.append(coder.toString());
        }
        ret.append(", userType=");
        ret.append(userType);
        ret.append("]");
        return ret.toString();
    }
}
