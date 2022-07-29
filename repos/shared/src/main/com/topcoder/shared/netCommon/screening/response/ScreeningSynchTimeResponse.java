package com.topcoder.shared.netCommon.screening.response;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

import java.io.IOException;
import java.io.ObjectStreamException;

/**
 * Sends the current time on the server to the client.
 *
 * @author  Budi Kusmiantoro
 * @version
 */

public class ScreeningSynchTimeResponse extends ScreeningBaseResponse {

    /** time on server in milliseconds since 01/01/1970 00:00:00 GMT */
    long time;

    /**
     * Constructor for CS Handler.
     */
    public ScreeningSynchTimeResponse() {
        sync = false;
// just in case
        time = System.currentTimeMillis();
    }

    /**
     * @param time Time from server in milliseconds.
     */
    public ScreeningSynchTimeResponse(long time) {
        sync = false;
        this.time = time;
    }

    /**
     * Serializes the object
     *
     * @param writer the custom serialization writer
     * @throws IOException exception during writing
     *
     * @see com.topcoder.shared.netCommon.CSWriter
     * @see java.io.IOException
     */
    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        // for increased accuracy, send the time right as the object is being sent.
        writer.writeLong(System.currentTimeMillis());
    }

    /**
     * Creates the object from a serialization stream
     *
     * @param reader the custom serialization reader
     * @throws IOException           exception during reading
     * @throws ObjectStreamException exception during reading
     *
     * @see com.topcoder.shared.netCommon.CSWriter
     * @see java.io.IOException
     * @see java.io.ObjectStreamException
     */
    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        time = reader.readLong();
    }

    /**
     * @return Time from server in milliseconds.
     */
    public long getTime() {
        return time;
    }

    /**
     * Gets the string representation of this object
     *
     * @return the string representation of this object
     */
    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.shared.netCommon.screening.response.ScreeningSynchTimeResponse) [");
        ret.append("time = ");
        ret.append(time);
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }
}
