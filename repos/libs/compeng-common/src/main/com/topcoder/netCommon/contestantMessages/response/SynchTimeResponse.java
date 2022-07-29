package com.topcoder.netCommon.contestantMessages.response;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a response to send the current time on the server to the client.<br>
 * Use: When the client receives this response, the time in this response should be used to calculate the phase
 * schedules. This response may be sent as part of responses or directly by server without any request.
 * 
 * @author Matthew P. Suhocki (msuhocki)
 * @version $Id: SynchTimeResponse.java 72343 2008-08-15 06:09:22Z qliu $
 */
public class SynchTimeResponse extends BaseResponse {
    /** Represents the time on server in milliseconds since 01/01/1970 00:00:00 GMT. */
    long time;

    /**
     * Creates a new instance of <code>SynchTimeResponse</code>. It is required by custom serialization.
     */
    public SynchTimeResponse() {
        // just in case
        time = System.currentTimeMillis();
    }

    /**
     * Creates a new instance of <code>SynchTimeResponse</code>. The time is represented as milliseconds since
     * 01/01/1970 00:00:00 GMT.
     * 
     * @param time the time on server.
     * @see java.util.Date#getTime()
     */
    public SynchTimeResponse(long time) {
        this.time = time;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        // for increased accuracy, send the time right as the object is being sent.
        writer.writeLong(System.currentTimeMillis());
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        time = reader.readLong();
    }

    /**
     * Gets the time on server The time is represented as milliseconds since 01/01/1970 00:00:00 GMT.
     * 
     * @return the time on server.
     * @see java.util.Date#getTime()
     */
    public long getTime() {
        return time;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.response.SynchTimeResponse) [");
        ret.append("time = ");
        ret.append(time);
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }
}
