package com.topcoder.shared.netCommon.screening.request;

import com.topcoder.shared.netCommon.screening.ScreeningConstants;

/**
 * <p>Title: ScreeningErrorRequest</p>
 * <p>Description: </p>
 * @author bkus
 */
public class ScreeningErrorRequest extends ScreeningBaseRequest {

    public ScreeningErrorRequest(){
        sync = false;
    }

    public int getRequestType() {
        return ScreeningConstants.ERROR;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.shared.netCommon.screening.request.ScreeningErrorRequest) [");
        ret.append("]");
        return ret.toString();
    }
}
