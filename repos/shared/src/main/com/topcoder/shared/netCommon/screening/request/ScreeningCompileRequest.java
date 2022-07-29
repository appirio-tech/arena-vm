package com.topcoder.shared.netCommon.screening.request;

import java.io.IOException;

import com.topcoder.shared.netCommon.screening.ScreeningConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Request that the server compiles a componentID/problemType-combination.
 */
public class ScreeningCompileRequest extends ScreeningBaseRequest {

    protected long componentID;
    protected int problemType; //EXAMPLE, SRM, COMPANY
    protected int languageID;
    protected String code;

    public ScreeningCompileRequest() {
    }

    public ScreeningCompileRequest(
            long componentID,
            int problemType,
            int languageID,
            String code) {

        this.componentID = componentID;
        this.problemType = problemType;
        this.languageID = languageID;
        this.code = code;
    }

    public int getRequestType() {
        return ScreeningConstants.COMPILE;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeLong(componentID);
        writer.writeInt(problemType);
        writer.writeInt(languageID);
        writer.writeString(code);
    }

    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        componentID = reader.readLong();
        problemType = reader.readInt();
        languageID = reader.readInt();
        code = reader.readString();
    }

    public int getLanguage() {
        return languageID;
    }

    public String getCode() {
        return code;
    }

    public long getComponentID() {
        return componentID;
    }

    public int getProblemType() {
        return problemType;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append(
                "(com.topcoder.shared.netCommon.screening.request.ScreeningCompileRequest) [");
        ret.append("componentID = ");
        ret.append(componentID);
        ret.append(", ");
        ret.append("problemType = ");
        ret.append(problemType);
        ret.append(", ");
        ret.append("language = ");
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
