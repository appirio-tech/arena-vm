package com.topcoder.shared.netCommon.screening.request;

import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.screening.ScreeningConstants;

import java.io.IOException;

//an empty request, just to prevent things from timing out

public class ScreeningKeepAliveRequest extends ScreeningBaseRequest {

    public ScreeningKeepAliveRequest() {
        sync = false;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
    }

    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
    }

    public int getRequestType() {
        return ScreeningConstants.KEEP_ALIVE_REQUEST;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.shared.netCommon.screening.request.ScreeningKeepAliveRequest) [");
        ret.append("]");
        return ret.toString();
    }
}
