package com.topcoder.netCommon.contestantMessages.response;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.netCommon.contestantMessages.AdminBroadcast;
import com.topcoder.netCommon.contestantMessages.ComponentBroadcast;
import com.topcoder.netCommon.contestantMessages.RoundBroadcast;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a response to notify the client that there is a new admin broadcast. The admin broadcast can be a general
 * broadcast, a round-specific broadcast, or a problem component-specific broadcast.<br>
 * Use: This response is sent directly by server without corresponding request. The client should notify the current
 * user immediately upon receiving this response.
 * 
 * @author Lars Backstrom
 * @version $Id: SingleBroadcastResponse.java 72343 2008-08-15 06:09:22Z qliu $
 * @see AdminBroadcast
 * @see RoundBroadcast
 * @see ComponentBroadcast
 */
public class SingleBroadcastResponse extends BaseResponse {
    /** Represents the new admin broadcast. */
    AdminBroadcast ab;

    /**
     * Creates a new instance of <code>SingleBroadcastResponse</code>. It is required by custom serialization.
     */
    public SingleBroadcastResponse() {
    }

    /**
     * Creates a new instance of <code>SingleBroadcastResponse</code>.
     * 
     * @param ab the new admin broadcast.
     */
    public SingleBroadcastResponse(AdminBroadcast ab) {
        this.ab = ab;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeObject(ab);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        ab = (AdminBroadcast) reader.readObject();
    }

    /**
     * Gets the new admin broadcast.
     * 
     * @return the new admin broadcast.
     */
    public AdminBroadcast getBroadcast() {
        return ab;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.response.SingleBroadcastResponse) [");
        ret.append("ab = ");
        if (ab == null) {
            ret.append("null");
        } else {
            ret.append(ab.toString());
        }
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }

    public int hashCode() {
        return ab.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj instanceof SingleBroadcastResponse) {
            SingleBroadcastResponse response = (SingleBroadcastResponse) obj;
            return ab.equals(response.ab);
        } else
            return false;
    }
}
