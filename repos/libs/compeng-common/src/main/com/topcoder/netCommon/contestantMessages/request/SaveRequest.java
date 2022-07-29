package com.topcoder.netCommon.contestantMessages.request;

import java.io.IOException;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a request to save the current code in the coding window to the server.<br>
 * Use: During coding, the current user may want to save the partial uncompilable code to the server, so that he can
 * resume coding afterwards. In this case, this request is sent.<br>
 * Note: The current user must in the room he is assigned, and the current phase must be coding phase. The problem
 * component must be open for coding.
 * 
 * @author Walter Mundt
 * @version $Id: SaveRequest.java 72292 2008-08-12 09:10:29Z qliu $
 * @see OpenComponentForCodingRequest
 * @see CompileRequest
 */
public class SaveRequest extends BaseRequest {
    /** Represents the source code to be saved. */
    protected String code;

    /** Represents the ID of the problem component. */
    protected int componentID;

    /** Represents the ID of the programming language. */
    protected int languageID;

    /**
     * Creates a new instance of <code>SaveRequest</code>. It is required by custom serialization.
     */
    public SaveRequest() {
    }

    /**
     * Creates a new instance of <code>SaveRequest</code>.
     * 
     * @param code the source code to be saved.
     * @param componentID the ID of the problem component.
     * @param languageID the ID of the programming language.
     */
    public SaveRequest(String code, int componentID, int languageID) {
        this.code = code;
        this.componentID = componentID;
        this.languageID = languageID;
    }

    public int getRequestType() {
        return ContestConstants.SAVE;
    }

    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        code = reader.readString();
        componentID = reader.readInt();
        languageID = reader.readInt();
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeString(code);
        writer.writeInt(componentID);
        writer.writeInt(languageID);
    }

    /**
     * Gets the source code to be saved.
     * 
     * @return the source code.
     */
    public String getCode() {
        return code;
    }

    /**
     * Gets the ID of the problem component.
     * 
     * @return the problem component ID.
     */
    public int getComponentID() {
        return componentID;
    }

    /**
     * Gets the ID of the programming language.
     * 
     * @return the programming language ID.
     */
    public int getLanguageID() {
        return languageID;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.request.SaveRequest) [");
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
