package com.topcoder.netCommon.contestantMessages.response;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.util.HashMap;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a response to notify the client that the preferences for the current user saved on the server has been
 * changed.<br>
 * Use: When a user updates the preferences, this response is sent. The preferences in this response should replace any
 * previous preferences at the client.<br>
 * Note: This response is not supported any more.
 * 
 * @author Lars Backstrom
 * @version $Id: UpdatePreferencesResponse.java 72385 2008-08-19 07:00:36Z qliu $
 */
public class UpdatePreferencesResponse extends BaseResponse {
    /** Represents the updated preferences saved on the server. */
    private HashMap preferences;

    /**
     * Creates a new instance of <code>UpdatePreferencesResponse</code>. It is required by custom serialization.
     */
    public UpdatePreferencesResponse() {
    }

    /**
     * Creates a new instance of <code>UpdatePreferencesResponse</code>. There is no copy.
     * 
     * @param preferences the updated preferences saved on server.
     */
    public UpdatePreferencesResponse(HashMap preferences) {
        this.preferences = preferences;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeObject(preferences);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        preferences = (HashMap) (reader.readObject());
    }

    /**
     * Gets the updated preferences saved on server. There is no copy.
     * 
     * @return the preferences.
     */
    public HashMap getPreferences() {
        return preferences;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.response.UpdatePreferencesResponse) [");
        ret.append("preferences = ");
        if (preferences == null) {
            ret.append("null");
        } else {
            ret.append(preferences.toString());
        }
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }

}
