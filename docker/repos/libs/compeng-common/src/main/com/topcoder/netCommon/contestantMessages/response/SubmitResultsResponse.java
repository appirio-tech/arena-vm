package com.topcoder.netCommon.contestantMessages.response;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a response to notify the client about the result of a submission request.<br>
 * Use: This response is specific to <code>SubmitRequest</code>. The result of the submission is presented as a
 * message instead of a boolean value. The message should be shown to the current user. There is also a flag indicating
 * if the solution is ready to be system tested.<br>
 * Note: For some contests, such as Sun Java onsite, the system test result is sent immediately after finishing the
 * test, instead of until the system test phase. In this case, the client should wait until the system test result
 * arrives.
 * 
 * @author Ryan Fairfax
 * @version $Id: SubmitResultsResponse.java 72343 2008-08-15 06:09:22Z qliu $
 */
public class SubmitResultsResponse extends BaseResponse {
    /** Represents the message of the submission result. */
    private String msg;

    /** Represents the ID of the round. */
    private long roundID;

    /** Represents a flag indicating if the solution is ready to be system tested. */
    private boolean bSystest;

    /**
     * Creates a new instance of <code>SubmitResultsResponse</code>. It is required by custom serialization.
     */
    public SubmitResultsResponse() {
    }

    /**
     * Creates a new instance of <code>SubmitResultsResponse</code>.
     * 
     * @param msg the message of the submission result.
     * @param roundID the ID of the round.
     * @param bSystest <code>true</code> if the solution is ready to be system tested; <code>false</code> otherwise.
     */
    public SubmitResultsResponse(String msg, long roundID, boolean bSystest) {
        this.msg = msg;
        this.roundID = roundID;
        this.bSystest = bSystest;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeString(msg);
        writer.writeLong(roundID);
        writer.writeBoolean(bSystest);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        msg = reader.readString();
        roundID = reader.readLong();
        bSystest = reader.readBoolean();
    }

    /**
     * Gets the message of the submission result.
     * 
     * @return the message of the submission result.
     */
    public String getMessage() {
        return msg;
    }

    /**
     * Gets the ID of the round.
     * 
     * @return the round ID.
     */
    public long getRoundID() {
        return roundID;
    }

    /**
     * Gets a flag indicating if the solution is ready to be system tested.
     * 
     * @return <code>true</code> if the solution is ready to be system tested; <code>false</code> otherwise.
     */
    public boolean getSystest() {
        return bSystest;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.response.SubmitResultsResponse) [");
        ret.append("msg = ");
        if (msg == null) {
            ret.append("null");
        } else {
            ret.append(msg.toString());
        }
        ret.append(", ");
        ret.append("roundID =  ");
        ret.append(roundID);
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }

}