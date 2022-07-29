package com.topcoder.netCommon.contestantMessages.request;

import java.io.IOException;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a request to store the preferences of default programming language on server.<br>
 * Use: When a user chooses the default programming language in the arena, it should be stored on the server by this
 * request.<br>
 * 
 * @author Mark Tong
 * @version $Id: SetLanguageRequest.java 72292 2008-08-12 09:10:29Z qliu $
 */
public class SetLanguageRequest extends BaseRequest {
    /** Represents the ID of the default programming language selected by the current user. */
    int languageID;

    /**
     * Creates a new instance of <code>SetLanguageRequest</code>. It is required by custom serialization.
     */
    public SetLanguageRequest() {
    }

    /**
     * Creates a new instance of <code>SetLanguageRequest</code>.
     * 
     * @param languageID the ID of the default programming language selected.
     */
    public SetLanguageRequest(int languageID) {
        this.languageID = languageID;
    }

    public int getRequestType() {
        return ContestConstants.SET_LANGUAGE;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeInt(languageID);
    }

    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        languageID = reader.readInt();
    }

    /**
     * Gets the ID of the default programming language selected.
     * 
     * @return the default programming language ID.
     */
    public int getLanguageID() {
        return languageID;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.request.SetLanguageRequest) [");
        ret.append("languageID = ");
        ret.append(languageID);
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }
}
