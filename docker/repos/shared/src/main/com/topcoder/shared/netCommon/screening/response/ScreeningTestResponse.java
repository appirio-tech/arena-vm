package com.topcoder.shared.netCommon.screening.response;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Response sent after the client has sent a test-request.
 * @author Peter Fornwall (FatClimber)
 */
public class ScreeningTestResponse extends ScreeningBaseResponse {

    /** status of test-task, true if sourcecode was successfully tested on
     *  the server */
    private boolean status;

    /** message to accompany the test-response */
    private String msg;

    /**
     * Constructor needed for CS.
     */
    public ScreeningTestResponse() {
    }

    /**
     * Main constructor.
     * @param status - true if success.
     * @param msg - message.
     */
    public ScreeningTestResponse(boolean status, String msg) {
        super();
        this.status = status;
        this.msg = msg;
    }

    /**
     * Gets the status of a test.
     * @return boolean - true if success.
     */
    public boolean getStatus() {
        return status;
    }

    /**
     * Gets the test-message.
     * @return String - the test-message.
     */
    public String getMessage() {
        return msg;
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
        writer.writeBoolean(status);
        writer.writeString(msg);
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
    public void customReadObject(CSReader reader)
            throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        status = reader.readBoolean();
        msg = reader.readString();
    }

    /**
     * Gets the string representation of this object
     *
     * @return the string representation of this object
     */
    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append(
                "(com.topcoder.shared.netCommon.screening.response.ScreeningTestResponse) [");
        ret.append("status = ");
        ret.append(status);
        ret.append(", ");
        ret.append("msg = ");
        ret.append(msg.toString());
        ret.append("]");
        return ret.toString();
    }
}
