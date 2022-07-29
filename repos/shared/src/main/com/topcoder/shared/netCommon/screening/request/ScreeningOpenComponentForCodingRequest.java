package com.topcoder.shared.netCommon.screening.request;

import java.io.IOException;

import com.topcoder.shared.netCommon.screening.ScreeningConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

public class ScreeningOpenComponentForCodingRequest
        extends ScreeningBaseRequest {

    protected long componentID;
    protected int problemType; //EXAMPLE, SRM, COMPANY

    public ScreeningOpenComponentForCodingRequest() {
    }

    public ScreeningOpenComponentForCodingRequest(
            long componentID,
            int problemType) {

        this.componentID = componentID;
        this.problemType = problemType;
    }

    public long getComponentID() {
        return componentID;
    }

    public int getProblemType() {
        return problemType;
    }

    public int getRequestType() {
        return ScreeningConstants.GET_PROBLEM;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeLong(componentID);
        writer.writeInt(problemType);
    }

    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        componentID = reader.readLong();
        problemType = reader.readInt();
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append(
                "(com.topcoder.shared.netCommon.screening.request.ScreeningOpenComponentForCodingRequest) [");
        ret.append("componentID = ");
        ret.append(componentID);
        ret.append(", ");
        ret.append("problemType = ");
        ret.append(problemType);
        ret.append("]");
        return ret.toString();
    }
}
