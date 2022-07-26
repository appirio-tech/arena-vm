package com.topcoder.shared.netCommon.screening.request;

import java.io.IOException;

import com.topcoder.shared.netCommon.screening.ScreeningConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

public class ScreeningTermsRequest extends ScreeningBaseRequest {

    protected long companyID;

    public ScreeningTermsRequest() {
        this.sync = false;
    }

    public ScreeningTermsRequest(
            long companyID) {
        this.sync = false;
        this.companyID = companyID;
    }

    public long getCompanyID() {
        return companyID;
    }

    public int getRequestType() {
        return ScreeningConstants.TERMS_REQUEST;
    }

    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        companyID = reader.readLong();
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeLong(companyID);
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append(
                "(com.topcoder.shared.netCommon.screening.request.ScreeningTermsRequest) [");
        ret.append("companyID = ");
        ret.append(companyID);
        ret.append("]");
        return ret.toString();
    }
}
