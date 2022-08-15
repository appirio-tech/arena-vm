package com.topcoder.netCommon.contestantMessages.response;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.util.ArrayList;

import com.topcoder.netCommon.contestantMessages.AdminBroadcast;
import com.topcoder.netCommon.contestantMessages.ComponentBroadcast;
import com.topcoder.netCommon.contestantMessages.RoundBroadcast;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a response to send all admin broadcasts to the client.<br>
 * Use: This response is specific to <code>GetAdminBroadcastRequest</code>. It returns all previous sent broadcasts
 * to the client. The client should show the list of broadcasts to the current user.<br>
 * Note: The admin broadcasts can be <code>AdminBroadcast</code>, <code>RoundBroadcast</code> or
 * <code>ComponentBroadcast</code>.
 * 
 * @author Lars Backstrom
 * @version $Id: GetAdminBroadcastResponse.java 72313 2008-08-14 07:16:48Z qliu $
 * @see AdminBroadcast
 * @see RoundBroadcast
 * @see ComponentBroadcast
 */
public class GetAdminBroadcastResponse extends BaseResponse {
    /** Represents all admin broadcasts. */
    private ArrayList broadcasts;

    /**
     * Creates a new instance of <code>GetAdminBroadcastResponse</code>. It is required by custom serialization.
     */
    public GetAdminBroadcastResponse() {
    }

    /**
     * Creates a new instance of <code>GetAdminBroadcastResponse</code>. There is no copy.
     * 
     * @param broadcasts all admin broadcasts.
     */
    public GetAdminBroadcastResponse(ArrayList broadcasts) {
        this.broadcasts = broadcasts;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeArrayList(broadcasts);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        broadcasts = reader.readArrayList();
    }

    /**
     * Gets all admin broadcasts. There is no copy. The list contains instances of <code>AdminBroadcast</code>,
     * <code>RoundBroadcast</code> and <code>ComponentBroadcast</code>.
     * 
     * @return all admin broadcasts.
     */
    public ArrayList getBroadcasts() {
        return broadcasts;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.response.GetAdminBroadcastResponse) [");
        ret.append("broadcasts = ");
        if (broadcasts == null) {
            ret.append("null");
        } else {
            ret.append(broadcasts.toString());
        }
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }
}
