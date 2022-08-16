package com.topcoder.shared.netCommon.screening.request;

import java.io.IOException;
import java.util.ArrayList;

import com.topcoder.shared.netCommon.screening.ScreeningConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Request the server to test a componentID/problemType-combination using the
 * arguments supplied.
 */
public class ScreeningTestRequest extends ScreeningBaseRequest {

    protected long componentID;
    protected int problemType; //EXAMPLE, SRM, COMPANY
    protected ArrayList testArgs;

    public ScreeningTestRequest() {
    }

    public ScreeningTestRequest(
            ArrayList args,
            long componentID,
            int problemType) {

        testArgs = args;
        this.componentID = componentID;
        this.problemType = problemType;
    }

    public long getComponentID() {
        return componentID;
    }

    public int getProblemType() {
        return problemType;
    }

    public ArrayList getArgs() {
        return testArgs;
    }

    public int getRequestType() {
        return ScreeningConstants.TEST;
    }

    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        testArgs = reader.readArrayList();
        componentID = reader.readLong();
        problemType = reader.readInt();
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeArrayList(testArgs);
        writer.writeLong(componentID);
        writer.writeInt(problemType);
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append(
                "(com.topcoder.shared.netCommon.screening.request.ScreeningTestRequest) [");
        ret.append("testArgs = ");
        if (testArgs == null) {
            ret.append("null");
        } else {
            ret.append(testArgs.toString());
        }
        ret.append(", ");
        ret.append("componentID = ");
        ret.append(componentID);
        ret.append(", ");
        ret.append("problemType = ");
        ret.append(problemType);
        ret.append("]");
        return ret.toString();
    }
}
