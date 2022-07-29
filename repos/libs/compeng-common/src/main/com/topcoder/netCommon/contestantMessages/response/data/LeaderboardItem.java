package com.topcoder.netCommon.contestantMessages.response.data;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;

import com.topcoder.netCommon.contestantMessages.response.CreateLeaderBoardResponse;
import com.topcoder.netCommon.contestantMessages.response.UpdateLeaderBoardResponse;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

/**
 * Defines the leader of a room in the leader board. The rating will be proper type according to the type of the round
 * of the room. The seed in a room is calculated by the rating rank.
 * 
 * @author Michael Cervantes
 * @version $Id: LeaderboardItem.java 72424 2008-08-20 08:06:01Z qliu $
 * @see CreateLeaderBoardResponse
 * @see UpdateLeaderBoardResponse
 */
public class LeaderboardItem implements CustomSerializable, Serializable {
    /** Represents the ID of the leader's room. */
    private long roomID;

    /** Represents the handle of the leader. */
    private String userName;

    /** Represents the rating of the leader. */
    private int userRating;

    /** Represents the seed of the leader in the room. */
    private int seed;

    /** Represents the current total score of the leader. */
    private double points;

    /** Represents a flag indicating if the current total score of runners-up is close to the leader. */
    private boolean closeRace;

    /**
     * Creates a new instance of <code>LeaderboardItem</code>. It is required by custom serialization.
     */
    public LeaderboardItem() {
    }

    /**
     * Creates a new instance of <code>LeaderboardItem</code>.
     * 
     * @param roomID the ID of the leader's room.
     * @param userName the handle of the leader.
     * @param userRating the rating of the leader.
     * @param seed the seed of the leader in the room.
     * @param points the current total score of the leader.
     * @param closeRace <code>true</code> if the current total score of runners-up is close to the leader;
     *            <code>false</code> otherwise.
     */
    public LeaderboardItem(long roomID, String userName, int userRating, int seed, double points, boolean closeRace) {
        this.userName = userName;
        this.userRating = userRating;
        this.roomID = roomID;
        this.points = points;
        this.seed = seed;
        this.closeRace = closeRace;
    }

    public void customWriteObject(CSWriter csWriter) throws IOException {
        csWriter.writeString(userName);
        csWriter.writeInt(userRating);
        csWriter.writeLong(roomID);
        csWriter.writeDouble(points);
        csWriter.writeBoolean(closeRace);
        csWriter.writeInt(seed);
    }

    public void customReadObject(CSReader csReader) throws IOException, ObjectStreamException {
        userName = csReader.readString();
        userRating = csReader.readInt();
        roomID = csReader.readLong();
        points = csReader.readDouble();
        closeRace = csReader.readBoolean();
        seed = csReader.readInt();
    }

    /**
     * Gets the handle of the leader.
     * 
     * @return the handle of the leader.
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Gets the rating of the leader.
     * 
     * @return the rating of the leader.
     */
    public int getUserRating() {
        return userRating;
    }

    /**
     * Gets the ID of the leader's room.
     * 
     * @return the room ID.
     */
    public long getRoomID() {
        return roomID;
    }

    /**
     * Gets the current total score of the leader.
     * 
     * @return the current total score of the leader.
     */
    public double getPoints() {
        return points;
    }

    /**
     * Gets a flag indicating if the current total score of runners-up is close to the leader.
     * 
     * @return <code>true</code> if the current total score of runners-up is close to the leader; <code>false</code>
     *         otherwise.
     */
    public boolean isCloseRace() {
        return closeRace;
    }

    /**
     * Gets the seed of the leader in the room.
     * 
     * @return the seed of the leader in the room.
     */
    public int getSeed() {
        return seed;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.response.data.LeaderboardItem) [");
        ret.append("userName = ");
        if (userName == null) {
            ret.append("null");
        } else {
            ret.append(userName.toString());
        }
        ret.append(", ");
        ret.append("userRating = ");
        ret.append(userRating);
        ret.append(", ");
        ret.append("roomID = " + roomID);
        ret.append("]");
        return ret.toString();
    }
}