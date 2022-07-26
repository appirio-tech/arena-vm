package com.topcoder.netCommon.contestantMessages.response;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a response to enable a round. Once the round is enabled, the current user may register for the round or enter
 * the contest room of the round.<br>
 * Use: This response is sent by the server directly without any corresponding request. Once this response is received,
 * the registration and entering contest room mechanisms for the corresponding round should be enabled on the client.<br>
 * 
 * @author Lars Backstrom
 * @version $Id: EnableRoundResponse.java 72300 2008-08-13 08:33:29Z qliu $
 */
public class EnableRoundResponse extends BaseResponse {
    /** Represents the ID of the round. */
    private long roundID;

    /**
     * Creates a new instance of <code>EnableRoundResponse</code>. It is required by custom serialization.
     */
    public EnableRoundResponse() {
    }

    /**
     * Creates a new instance of <code>EnableRoundResponse</code>.
     * 
     * @param roundID the ID of the round to be enabled.
     */
    public EnableRoundResponse(long roundID) {
        this.roundID = roundID;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeLong(roundID);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        roundID = reader.readLong();
    }

    /**
     * Gets the ID of the round to be enabled.
     * 
     * @return the ID of the round to be enabled.
     */
    public long getRoundID() {
        return roundID;
    }

    public String toString() {
        return "(EnableRoundResponse)[" + roundID + "]";
    }

}
