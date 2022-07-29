/**
 * LoginResponse.java Description: Specifies a login response for both spectator and contest applets
 * 
 * @author Lars Backstrom
 */

package com.topcoder.netCommon.contestantMessages.response;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a response to forcely log the current user out. This response also contains a message describing why the
 * current user is forcely logged out.<br>
 * Use: This response is sent by server without client sending any requests. When receiving this response, the client
 * should log the current user out immediately, and show the message to the user (e.g. in a dialog).
 * 
 * @author Lars Backstrom
 * @version $Id: ForcedLogoutResponse.java 72300 2008-08-13 08:33:29Z qliu $
 */
public class ForcedLogoutResponse extends BaseResponse {
    /** Represents the title of the message. */
    private String title;

    /** Represents the message of the reason. */
    private String msg;

    /**
     * Creates a new instance of <code>ForcedLogoutResponse</code>. It is required by custom serialization.
     */
    public ForcedLogoutResponse() {
    }

    /**
     * Creates a new instance of <code>ForcedLogoutResponse</code>.
     * 
     * @param title the title of the message.
     * @param msg the message of the reason to be forced logging out.
     */
    public ForcedLogoutResponse(String title, String msg) {
        super();
        this.title = title;
        this.msg = msg;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeString(title);
        writer.writeString(msg);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        title = reader.readString();
        msg = reader.readString();
    }

    /**
     * Gets the title of the message.
     * 
     * @return the title of the message.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the message of the reason to be forced logging out.
     * 
     * @return the message of the reason to be forced logging out.
     */
    public String getMessage() {
        return msg;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.response.ForcedLogoutResponse) [");
        ret.append("title = ");
        if (title == null) {
            ret.append("null");
        } else {
            ret.append(title.toString());
        }
        ret.append(", ");
        ret.append("msg = ");
        if (msg == null) {
            ret.append("null");
        } else {
            ret.append(msg.toString());
        }
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }

}