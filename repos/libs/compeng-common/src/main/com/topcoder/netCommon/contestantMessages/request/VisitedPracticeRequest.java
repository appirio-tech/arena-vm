package com.topcoder.netCommon.contestantMessages.request;

import java.io.IOException;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a request to get the list of practice rooms visited by the current user. There is no payload.<br>
 * Use: The current user may get all visited practice rooms by this request.
 * 
 * @author Qi Liu
 * @version $Id: VisitedPracticeRequest.java 72292 2008-08-12 09:10:29Z qliu $
 */
public class VisitedPracticeRequest extends BaseRequest {
    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
    }
    
    public int getRequestType() {
        return ContestConstants.VISITED_PRACTICE;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.request.VisitedPracticeRequest) [");
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }
}

