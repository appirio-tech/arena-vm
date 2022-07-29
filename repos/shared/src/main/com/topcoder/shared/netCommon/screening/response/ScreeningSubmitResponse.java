package com.topcoder.shared.netCommon.screening.response;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.problem.ProblemComponent;

/**
 * Response sent after the client has sent a submit-request.
 * Possible status-values are:
 * ERROR - error while submitting.
 * SUCCESS -  successfully submitted.
 * RESUBMIT - to ask if client wants to resubmit.
 * @author Peter Fornwall (FatClimber)
 */
public class ScreeningSubmitResponse extends ScreeningBaseResponse {

    public static final int ERROR = 0;
    public static final int SUCCESS = 1;
    public static final int RESUBMIT = 2;

    /** The status of a submit */
    private int status;

    /** message to accompany the submit-response */
    private String msg;
    private ProblemComponent comp;
    
    private long openTime;
    private long length;

    /**
     * Constructor needed for CS.
     */
    public ScreeningSubmitResponse() {
    }

    public ProblemComponent getProblemComponent() {
        return comp;
    }

    /**
     * Constructor that only sets status.
     * @param status - ERROR/SUCCESS/RESUBMIT.
     */
    public ScreeningSubmitResponse(int status, ProblemComponent comp, long openTime, long length) {
        this(status, null,comp, openTime, length);
    }

    /**
     * Main constructor.
     * @param status - ERROR/SUCCESS/RESUBMIT.
     * @param msg - message.
     */
    public ScreeningSubmitResponse(int status, String msg, ProblemComponent comp, long openTime, long length) {
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

    /**
     * Gets the status of a submit.
     * @return int - ERROR/SUCCESS/RESUBMIT.
     */
    public int getStatus() {
        return status;
    }

    /**
     * Gets the submit-message.
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
        writer.writeInt(status);
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
        status = reader.readInt();
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
                "(com.topcoder.shared.netCommon.screening.response.ScreeningSubmitResponse) [");
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
