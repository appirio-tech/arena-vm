package com.topcoder.netCommon.contestantMessages.request;

import java.io.IOException;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a request to get the current arena applet version. It has no payload.<br>
 * Use: At present, there is no use.<br>
 * 
 * @author Ryan Fairfax
 * @version $Id: GetCurrentAppletVersionRequest.java 72163 2008-08-07 07:51:04Z qliu $
 */
public class GetCurrentAppletVersionRequest extends BaseRequest {
    /**
     * Creates a new instance of <code>GetCurrentAppletVersionRequest</code>. It is required by custom serialization.
     */
    public GetCurrentAppletVersionRequest() {
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
    }

    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
    }

    public int getRequestType() {
        return ContestConstants.GET_CURRENT_APPLET_VERSION_REQUEST;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.request.GetCurrentAppletVersionRequest) [");
        ret.append("]");
        return ret.toString();
    }
}
