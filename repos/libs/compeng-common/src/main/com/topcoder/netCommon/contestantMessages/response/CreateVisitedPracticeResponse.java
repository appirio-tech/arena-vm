package com.topcoder.netCommon.contestantMessages.response;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a response to send the list of all visited practice rooms for the current user of the receiver.<br>
 * Use: This response is specific to <code>VisitedPracticeRequest</code>. The client should show the current user the
 * list of visited practice rooms in this response.<br>
 * Note: For each practice room, there is a corresponding practice round.
 * 
 * @author Griffin Dorman
 * @version $Id: CreateVisitedPracticeResponse.java 72300 2008-08-13 08:33:29Z qliu $
 */
public class CreateVisitedPracticeResponse extends BaseResponse {
    /** Represents the IDs of the visited practice rounds. */
    private int[] roundIDs;

    /**
     * Creates a new instance of <code>CreateVisitedPracticeResponse</code>. It is required by custom serialization.
     */
    public CreateVisitedPracticeResponse() {
    }

    /**
     * Creates a new instance of <code>CreateVisitedPracticeResponse</code>.
     * 
     * @param roundIDs the IDs of rounds of visited practice rooms.
     */
    public CreateVisitedPracticeResponse(int[] roundIDs) {
        this.roundIDs = roundIDs;
    }

    /**
     * Gets the IDs of rounds of visited practice rooms.
     * 
     * @return the IDs of rounds of visited practice rooms.
     */
    public int[] getRoundIDs() {
        return roundIDs;
    }

    public void customReadObject(CSReader csReader) throws IOException, ObjectStreamException {
        super.customReadObject(csReader);
        roundIDs = (int[]) csReader.readObject();
    }

    public void customWriteObject(CSWriter csWriter) throws IOException {
        super.customWriteObject(csWriter);
        csWriter.writeObject(roundIDs);
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.response.CreateVisitedPracticeResponse) [");
        ret.append("roundData = ");
        if (roundIDs == null) {
            ret.append("null");
        } else {
            ret.append("{");
            for (int i = 0; i < roundIDs.length; i++) {
                ret.append(roundIDs[i] + ",");
            }
            ret.append("}");
        }
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }
}
