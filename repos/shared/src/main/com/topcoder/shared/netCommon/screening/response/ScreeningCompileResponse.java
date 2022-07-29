package com.topcoder.shared.netCommon.screening.response;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.problem.ProblemComponent;

/**
 * Response sent after the client has sent a compile-request.
 * @author Peter Fornwall (FatClimber)
 */
public class ScreeningCompileResponse extends ScreeningBaseResponse {

    /** status of compile-task, true if sourcecode was successfully compiled on
     *  the server */
    private boolean status;

    /** message to accompany the compile-response(errors/warnings/success) */
    private String msg;
    private ProblemComponent comp;
    
    private long openTime;
    private long length;

    /**
     * Constructor needed for CS.
     */
    public ScreeningCompileResponse() {
    }

    /**
     * Constructor that only sets status.
     * @param status - true if success.
     */
    public ScreeningCompileResponse(boolean status,ProblemComponent comp, long openTime, long length) {
        this(status, null, comp, openTime, length);
    }

    /**
     * Main constructor.
     * @param status - true if success.
     * @param msg - message(errors/warnings/success).
     */
    public ScreeningCompileResponse(boolean status, String msg, ProblemComponent comp, long openTime, long length) {
        super();
        this.status = status;
        this.msg = msg;
        this.comp = comp;
        this.openTime = openTime;
        this.length = length;
    }
    
    public long getOpenTime() {
        return openTime;
    }
    
    public long getLength() {
        return length;
    }

    public ProblemComponent getProblemComponent() {
        return comp;
    }
    /**
     * Gets the status of a compile.
     * @return boolean - true if success.
     */
    public boolean getStatus() {
        return status;
    }

    /**
     * Gets the message(errors/warnings/success).
     * @return String - the error-message.
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
        writer.writeObject(comp);
        writer.writeLong(openTime);
        writer.writeLong(length);
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
        comp = (ProblemComponent)reader.readObject();
        openTime = reader.readLong();
        length = reader.readLong();
    }

    /**
     * Gets the string representation of this object
     *
     * @return the string representation of this object
     */
    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append(
                "(com.topcoder.shared.netCommon.screening.response.ScreeningCompileResponse) [");
        ret.append("status = ");
        ret.append(status);
        ret.append(", ");
        ret.append("msg = ");
        ret.append(msg.toString());
        ret.append(", ");
        ret.append("openTime = ");
        ret.append(openTime);
        ret.append(", ");
        ret.append("length = ");
        ret.append(length);
        ret.append("]");
        return ret.toString();
    }
}
