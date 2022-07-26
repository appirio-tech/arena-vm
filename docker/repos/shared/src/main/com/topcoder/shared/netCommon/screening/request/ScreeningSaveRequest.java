package com.topcoder.shared.netCommon.screening.request;

import java.io.IOException;

import com.topcoder.shared.netCommon.screening.ScreeningConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Requests the server to save the sourecode for a componentID/problemType-
 * combination.
 */
public class ScreeningSaveRequest extends ScreeningBaseRequest {

    protected long componentID;
    protected int problemType;
    protected int languageID;
    protected String code;

    public ScreeningSaveRequest() {
    }

    public ScreeningSaveRequest(
            long componentID,
            int problemType,
            int languageID,
            String code) {

        this.componentID = componentID;
        this.problemType = problemType;
        this.languageID = languageID;
        this.code = code;
    }

    public long getComponentID() {
        return componentID;
    }

    public int getProblemType() {
        return problemType;
    }

    public int getLanguageID() {
        return languageID;
    }

    public String getCode() {
        return code;
    }

    public int getRequestType() {
        return ScreeningConstants.SAVE;
    }

    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        code = reader.readString();
        componentID = reader.readLong();
        problemType = reader.readInt();
        languageID = reader.readInt();
        code = reader.readString();
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeString(code);
        writer.writeLong(componentID);
        writer.writeInt(problemType);
        writer.writeInt(languageID);
        writer.writeString(code);
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append(
                "(com.topcoder.shared.netCommon.screening.request.ScreeningSaveRequest) [");
        ret.append("componentID = ");
        ret.append(componentID);
        ret.append(", ");
        ret.append("problemType = ");
        ret.append(problemType);
        ret.append(", ");
        ret.append("languageID = ");
        ret.append(languageID);
        ret.append(", ");
        ret.append("code = ");
        if (code == null) {
            ret.append("null");
        } else {
            ret.append(code.toString());
        }
        ret.append("]");
        return ret.toString();
    }
}
