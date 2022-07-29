/*
 * SynchTimeRequest.java Created on November 9, 2005, 10:45 AM
 */

package com.topcoder.netCommon.contestantMessages.request;

import java.io.IOException;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a request to synchronize the time on the client to the server.<br>
 * Use: This request is sent regularly to synchronize the clock on the client side to the server side. By doing this,
 * the counting down for each phase would not off too much.
 * 
 * @author Mark Tong
 * @version $Id: SynchTimeRequest.java 72292 2008-08-12 09:10:29Z qliu $
 */
public class SynchTimeRequest extends BaseRequest {
    /** Represents the ID of the current connection. */
    private long connectionID;

    /**
     * Creates a new instance of <code>SynchRequest</code>. It is required by custom serialization.
     */
    public SynchTimeRequest() {
    }

    /**
     * Creates a new instance of <code>SynchRequest</code>.
     * 
     * @param cid the ID of the connection.
     */
    public SynchTimeRequest(long cid) {
        this.connectionID = cid;
    }

    /**
     * Gets the ID of the connection.
     * 
     * @return the ID of the connection.
     */
    public long getConnectionID() {
        return connectionID;
    }

    public int getRequestType() {
        return ContestConstants.SYNCH_TIME_REQUEST;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeLong(connectionID);
    }

    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        connectionID = reader.readLong();
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.request.SynchTimeRequest) [");
        ret.append("connectionid = ");
        ret.append(connectionID);
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }
}
