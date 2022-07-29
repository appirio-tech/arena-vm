/**
 * CreateChallengeTableResponse.java Description: Specifies a response for both spectator and contest applets
 * 
 * @author Lars Backstrom
 */

package com.topcoder.netCommon.contestantMessages.response;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.util.Arrays;

import com.topcoder.netCommon.contestantMessages.response.data.CoderItem;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a response to send the status of all coders in a room. The status includes the total score of each coder in
 * the room, as well as the status of all problem components of each coder in the room.<br>
 * Use: This response is used to establish the initial state of room summary and division summary (i.e. an aggregation
 * of room summaries). Any previous state of a room should be replaced by the data in this response.<br>
 * Note: This response is usually the first response to room-related subscription request. Subsequent update responses
 * are modifications to the state provided by this response.
 * 
 * @author Lars Backstrom
 * @version $Id: CreateChallengeTableResponse.java 72300 2008-08-13 08:33:29Z qliu $
 */
public class CreateChallengeTableResponse extends WatchableResponse {
    /** Represents the status of all coders in the room, including each problem component status. */
    private CoderItem[] coders;

    /**
     * Creates a new instance of <code>CreateChallengeTableResponse</code>. It is required by custom serialization.
     */
    public CreateChallengeTableResponse() {
        super(-1, -1);
    }

    /**
     * Creates a new instance of <code>CreateChallengeTableResponse</code>. There is no copy.
     * 
     * @param coders the status of all coders in the room.
     * @param roomType the type of the room.
     * @param roomID the ID of the room.
     */
    public CreateChallengeTableResponse(CoderItem[] coders, int roomType, int roomID) {
        super(roomType, roomID);
        this.coders = coders;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeObjectArray(coders);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        coders = (CoderItem[]) reader.readObjectArray(CoderItem.class);
    }

    /**
     * Gets the status of all coders in the room. The status includes each problem component status of all coders. There
     * is no copy.
     * 
     * @return the status of all coders in the room.
     */
    public CoderItem[] getCoders() {
        return coders;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.response.CreateChallengeTableResponse) [");
        ret.append("coders = ");
        if (coders == null) {
            ret.append("null");
        } else {
            ret.append(Arrays.asList(coders));
        }
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }
}