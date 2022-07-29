package com.topcoder.netCommon.contestantMessages.request;

import java.io.IOException;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a request to compile the code written by the current user for a problem component.<br>
 * Use: During the coding phase, when the current user wants to compile the code, this request should be sent.<br>
 * Note: The current user must in the room he is assigned, and the current phase must be coding phase. The problem
 * component must be open for coding. A compilation automatically saves the code.
 * 
 * @author Walter Mundt
 * @version $Id: CompileRequest.java 72292 2008-08-12 09:10:29Z qliu $
 */
public class CompileRequest extends BaseRequest {
    /** Represents the ID of the programming language of the code. */
    protected int language;

    /** Represents the source code to be compiled. */
    protected String code;

    /** Represents the ID of the problem component. */
    protected int componentID;

    /**
     * Creates a new instance of <code>CompileRequest</code>. It is required by custom serialization.
     */
    public CompileRequest() {
    }

    /**
     * Creates a new instance of <code>CompileRequest</code>.
     * 
     * @param language the ID of the programming language of the code.
     * @param componentID the ID of the problem component.
     * @param code the source code.
     */
    public CompileRequest(int language, int componentID, String code) {
        this.language = language;
        this.code = code;
        this.componentID = componentID;
    }

    public int getRequestType() {
        return ContestConstants.COMPILE;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeInt(language);
        writer.writeInt(componentID);
        writer.writeString(code);
    }

    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        language = reader.readInt();
        componentID = reader.readInt();
        code = reader.readString();
    }

    /**
     * Gets the ID of the programming language of the code.
     * 
     * @return the ID of the programming language.
     */
    public int getLanguage() {
        return language;
    }

    /**
     * Gets the source code to be compiled.
     * 
     * @return the source code.
     */
    public String getCode() {
        return code;
    }

    /**
     * Gets the ID of the problem component which the code is intended to solve.
     * 
     * @return the ID of the problem component.
     */
    public int getComponentID() {
        return componentID;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.request.CompileRequest) [");
        ret.append("language = ");
        ret.append(language);
        ret.append(", ");
        ret.append("code = ");
        if (code == null) {
            ret.append("null");
        } else {
            ret.append(code.toString());
        }
        ret.append(", ");
        ret.append("componentID = ");
        ret.append(componentID);
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }
}
