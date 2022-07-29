package com.topcoder.shared.netCommon.screening.request;

import com.topcoder.shared.netCommon.screening.ScreeningConstants;

/**
 * LogoutRequest to be sent when the user logs out.
 */
public class ScreeningLogoutRequest extends ScreeningBaseRequest {


    public ScreeningLogoutRequest() {
        sync=false;
    }

    public int getRequestType() {
        return ScreeningConstants.LOGOUT;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append(
                "(com.topcoder.shared.netCommon.screening.request.ScreeningLogoutRequest) [");
        ret.append("]");
        return ret.toString();
    }
}
