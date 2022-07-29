package com.topcoder.netCommon.contestantMessages.response;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a response to notify the client about the progress of system testing of a round.<br>
 * Use: This response is sent directly by server without any request during the system testing phase. It contains the
 * system testing progress of one round.
 * 
 * @author Lars Backstrom
 * @version $Id: SystestProgressResponse.java 72343 2008-08-15 06:09:22Z qliu $
 */
public class SystestProgressResponse extends BaseResponse {
    /** Represents the ID of the round. */
    private int roundID;

    /** Represents the number of finished system test cases. */
    private int done;

    /** Represents the number of all system test cases. */
    private int total;

    /**
     * Creates a new instance of <code>SystestProgressResponse</code>. It is required by custom serialization.
     */
    public SystestProgressResponse() {
    }

    /**
     * Creates a new instance of <code>SystestProgressResponse</code>.
     * 
     * @param done the number of finished system test cases.
     * @param total the number of all system test cases.
     * @param roundID the ID of the round.
     */
    public SystestProgressResponse(int done, int total, int roundID) {
        this.done = done;
        this.total = total;
        this.roundID = roundID;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeInt(done);
        writer.writeInt(total);
        writer.writeInt(roundID);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        done = reader.readInt();
        total = reader.readInt();
        roundID = reader.readInt();
    }

    /**
     * Gets the number of finished system test cases.
     * 
     * @return the number of finished system test cases.
     */
    public int getDone() {
        return done;
    }

    /**
     * Gets the number of all system test cases.
     * 
     * @return the number of all system test cases.
     */
    public int getTotal() {
        return total;
    }

    /**
     * Gets the ID of the round.
     * 
     * @return the round ID.
     */
    public int getRoundID() {
        return roundID;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.response.SystestProgressResponse) [");
        ret.append("roundID = ");
        ret.append(roundID);
        ret.append(", ");
        ret.append("done = ");
        ret.append(done);
        ret.append(", ");
        ret.append("total = ");
        ret.append(total);
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }
}
