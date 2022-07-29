package com.topcoder.shared.netCommon.screening.request;

import java.io.IOException;

import com.topcoder.shared.netCommon.screening.ScreeningConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

public final class ScreeningSubmitRequest extends ScreeningBaseRequest {

    long componentID;
    protected int problemType;  //EXAMPLE, SRM, COMPANY
    boolean resubmitFlag;  // true to confirm a resubmit

    public ScreeningSubmitRequest() {
    }

    public ScreeningSubmitRequest(long componentID, int problemType) {
        this(componentID, problemType, false);
    }

    public ScreeningSubmitRequest(long componentID, int problemType, boolean resubmitFlag) {
        this.componentID = componentID;
        this.problemType = problemType;
        this.resubmitFlag = resubmitFlag;
    }

    public int getRequestType() {
        return ScreeningConstants.SUBMIT_PROBLEM;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeLong(componentID);
        writer.writeInt(problemType);
        writer.writeBoolean(resubmitFlag);
    }

    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        componentID = reader.readLong();
        problemType = reader.readInt();
        resubmitFlag = reader.readBoolean();
    }

    public long getComponentID() {
        return componentID;
    }

    public int getProblemType() {
        return problemType;
    }

    public boolean getResubmitFlag() {
        return resubmitFlag;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.shared.netCommon.screening.request.ScreeningSubmitRequest) [");
        ret.append("componentID = ");
        ret.append(componentID);
        ret.append(", ");
        ret.append("problemType = ");
        ret.append(problemType);
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }

}
