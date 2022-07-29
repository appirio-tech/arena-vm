/**
 * CreateLeaderBoardResponse .java Description: Specifies a response for both spectator and contest applets
 * 
 * @author Lars Backstrom
 */

package com.topcoder.netCommon.contestantMessages.response;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.util.Arrays;

import com.topcoder.netCommon.contestantMessages.response.data.LeaderboardItem;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a response to send the status of all room leaders in a round. The status does not include detailed status of
 * each problem component of all room leaders. Use: This response is used to establish the initial state of the leader
 * board of a round. Any previous state of the leader board should be replaced by the data in this response.<br>
 * Note: This response is usually the first response to leader board subscription request. Subsequent update responses
 * are modifications to the state provided by this response.
 * 
 * @author Lars Backstrom
 * @version $Id: CreateLeaderBoardResponse.java 72300 2008-08-13 08:33:29Z qliu $
 */
public class CreateLeaderBoardResponse extends BaseResponse {
    /** Represents the status of all room leaders in the round. */
    private LeaderboardItem[] leaderboardItems;

    /** Represents the ID of the round. */
    private long roundID;

    /**
     * Creates a new instance of <code>CreateLeaderBoardResponse</code>. It is required by custom serialization.
     */
    public CreateLeaderBoardResponse() {
    }

    /**
     * Creates a new instance of <code>CreateLeaderBoardResponse</code>. There is no copy.
     * 
     * @param roundID the ID of the round.
     * @param leaderboardItems the status of all room leaders in the round.
     */
    public CreateLeaderBoardResponse(long roundID, LeaderboardItem[] leaderboardItems) {
        this.roundID = roundID;
        this.leaderboardItems = leaderboardItems;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeObjectArray(leaderboardItems);
        writer.writeLong(roundID);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        leaderboardItems = (LeaderboardItem[]) reader.readObjectArray(LeaderboardItem.class);
        roundID = reader.readLong();
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
     * Gets the status of all room leaders in the round. There is no copy.
     * 
     * @return the status of all room leaders in the round.
     */
    public LeaderboardItem[] getItems() {
        return leaderboardItems;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.response.CreateLeaderBoardResponse) [");
        ret.append("items = ");
        if (leaderboardItems == null) {
            ret.append("null");
        } else {
            ret.append(Arrays.asList(leaderboardItems).toString());
        }
        ret.append(", ");
        ret.append("roundID = ");
        ret.append(roundID);
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }
}
