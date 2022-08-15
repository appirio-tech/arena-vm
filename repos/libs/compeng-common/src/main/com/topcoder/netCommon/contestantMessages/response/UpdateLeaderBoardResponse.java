package com.topcoder.netCommon.contestantMessages.response;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.netCommon.contestantMessages.response.data.LeaderboardItem;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a response to update the leader of one room in a round in the leader board.<br>
 * Use: This response is sent to leader board subscribed clients only. Each response contains the updated information of
 * the leader of one room in a round. The previous leader information of the same room should be replaced by this one.
 * 
 * @author Lars Backstrom
 * @version $Id: UpdateLeaderBoardResponse.java 72385 2008-08-19 07:00:36Z qliu $
 */
public class UpdateLeaderBoardResponse extends BaseResponse {
    /** Represents the ID of the round. */
    private long roundID;

    /** Represents the leader information of a room in the round. */
    private LeaderboardItem item;

    /**
     * Creates a new instance of <code>UpdateLeaderBoardResponse</code>. It is required by custom serialization.
     */
    public UpdateLeaderBoardResponse() {
    }

    /**
     * Creates a new instance of <code>UpdateLeaderBoardResponse</code>.
     * 
     * @param roundID the ID of the round.
     * @param item the leader information of a room in the round.
     */
    public UpdateLeaderBoardResponse(long roundID, LeaderboardItem item) {
        this.roundID = roundID;
        this.item = item;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeLong(roundID);
        writer.writeObject(item);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        roundID = reader.readLong();
        item = (LeaderboardItem) reader.readObject();
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
     * Gets the leader information of a room in the round.
     * 
     * @return the leader information.
     */
    public LeaderboardItem getItem() {
        return item;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.response.UpdateLeaderBoardResponse) [");
        ret.append("round ID = ");
        ret.append(roundID);
        ret.append(", ");
        ret.append("item = ");
        if (item == null) {
            ret.append("null");
        } else {
            ret.append(item);
        }
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }

}
