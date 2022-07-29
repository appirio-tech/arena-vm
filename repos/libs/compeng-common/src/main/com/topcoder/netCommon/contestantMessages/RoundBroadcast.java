/**
 * A round-specific broadcast message.
 * 
 * @author Michael Cervantes (emcee)
 * @since Apr 6, 2002
 */
package com.topcoder.netCommon.contestantMessages;

import java.io.IOException;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a broadcast message from the admins to all registered users in a round.
 * 
 * @author Michael Cervantes (emcee)
 * @version $Id: RoundBroadcast.java 72093 2008-08-05 07:34:40Z qliu $
 */
public class RoundBroadcast extends AdminBroadcast {
    /** Represents the name of the round. */
    private String roundName;

    /** Represents the ID of the round. */
    protected int roundID;

    public int compareTo(Object o) {
        int sc = super.compareTo(o);
        if (sc != 0)
            return sc;
        RoundBroadcast rb = (RoundBroadcast) o;
        if (getType() != rb.getType()) {
            return getType() - rb.getType();
        }
        return roundID - rb.roundID;
    }

    /**
     * Gets the name of the round to be broadcasted.
     * 
     * @return the name of the round to be broadcasted.
     */
    public String getRoundName() {
        return roundName;
    }

    public int getType() {
        return ContestConstants.BROADCAST_TYPE_ADMIN_ROUND;
    }

    /**
     * Sets the name of the round to be broadcasted.
     * 
     * @param roundName the name of the round to be broadcasted.
     */
    public void setRoundName(String roundName) {
        this.roundName = roundName;
    }

    /**
     * Sets the ID of the round to be broadcasted.
     * 
     * @param roundID the ID of the round to be broadcasted.
     */
    public void setRoundID(int roundID) {
        this.roundID = roundID;
    }

    /**
     * Gets the ID of the round to be broadcasted.
     * 
     * @return the ID of the round to be broadcasted.
     */
    public int getRoundID() {
        return roundID;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeString(roundName);
        writer.writeInt(roundID);
    }

    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        roundName = reader.readString();
        roundID = reader.readInt();
    }

    public String toString() {
        return "round_broadcast(" + getTime() + ", " + getMessage() + ", " + getRoundID() + ", " + getRoundName() + ")";
    }

    public boolean equals(Object r) {
        if (r != null && r instanceof RoundBroadcast) {
            RoundBroadcast rhs = (RoundBroadcast) r;
            return rhs.getType() == getType() && rhs.getTime() == getTime() && rhs.getRoundID() == getRoundID()
                && (getRoundName() == null ? rhs.getRoundName() == null : getRoundName().equals(rhs.getRoundName()))
                && rhs.getMessage().equals(getMessage());
        }
        return false;
    }

    /**
     * Creates a new instance of <code>RoundBroadcast</code>. It is required by custom serialization.
     */
    public RoundBroadcast() {
    }

    /**
     * Creates a new instance of <code>RoundBroadcast</code>. The time, the message text, the round ID and the name
     * of the round are given.
     * 
     * @param time the time of the broadcast.
     * @param message the message text of the broadcast.
     * @param roundID the ID of the round to be broadcasted.
     * @param roundName the name of the round to be broadcasted.
     */
    public RoundBroadcast(long time, String message, int roundID, String roundName) {
        super(time, message);
        setRoundID(roundID);
        setRoundName(roundName);
    }
}
