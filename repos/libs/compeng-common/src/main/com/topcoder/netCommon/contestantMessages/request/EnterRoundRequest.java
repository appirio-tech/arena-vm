package com.topcoder.netCommon.contestantMessages.request;

import java.io.IOException;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a request to initiate the movement of the current user to a proper room in a round. The request will result
 * the server to send the room information together with phase information, problem components, users assigned to this
 * room, etc (if applicable).<br>
 * Use: When the current user wants to move to his assigned room or an admin room (if he is an admin) in a round, this
 * request should be sent. The only difference to <code>MoveRequest</code> would be, this request automatically
 * selects a room in a round while <code>MoveRequest</code> has to specify a room.<br>
 * Note: Loading the room information might take seconds to minutes depending on the size of the room and the network
 * bandwidth. It is recommended to use asynchronized way to send this request and display certain intermission UI when
 * waiting for the response.
 * 
 * @author Michael Cervantes
 * @version $Id: EnterRoundRequest.java 72343 2008-08-15 06:09:22Z qliu $
 * @see EnterRequest
 */
public class EnterRoundRequest extends BaseRequest {
    /** Represents the ID of the round whose room is chosen automatically to be moved to. */
    private long roundID;

    /**
     * Creates a new instance of <code>EnterRoundRequest</code>. It is required by custom serialization.
     */
    public EnterRoundRequest() {
    }

    /**
     * Creates a new instance of <code>EnterRoundRequest</code>.
     * 
     * @param roundID the ID of the round whose room is chosen.
     * @see #getRoundID()
     */
    public EnterRoundRequest(long roundID) {
        this.roundID = roundID;
    }

    public int getRequestType() {
        return ContestConstants.ENTER_ROUND;
    }

    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        roundID = reader.readLong();
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeLong(roundID);
    }

    /**
     * Gets the ID of the round whose room is chosen automatically to be moved to.
     * 
     * @return the ID of the round.
     */
    public long getRoundID() {
        return roundID;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.request.EnterRoundRequest) [");
        ret.append("roundID = ");
        ret.append(roundID);
        ret.append("]");
        return ret.toString();
    }
}
