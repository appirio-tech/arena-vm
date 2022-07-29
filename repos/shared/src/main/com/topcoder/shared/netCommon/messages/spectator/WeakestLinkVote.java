/**
 * WeakestLinkVote.java
 *
 * Description:         Informs the scoreboard of a vote to eliminate a coder
 * @author              Dave Pecora
 * @version             1.0
 */

package com.topcoder.shared.netCommon.messages.spectator;

import java.io.IOException;

import com.topcoder.shared.netCommon.messages.Message;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

public class WeakestLinkVote extends Message implements java.io.Serializable {

    /** The ID of the coder placing the vote */
    private int voterID;

    /** The ID of the coder voted against */
    private int victimID;

    /**
     * No-arg constructor needed by customserialization
     *
     */
    public WeakestLinkVote() {
    }

    /**
     * Constructs a weakest link vote message
     *
     * @param voterID the ID of the coder placing the vote
     * @param victimID the ID of the coder voted against
     */
    public WeakestLinkVote(int voterID, int victimID) {
        this.voterID = voterID;
        this.victimID = victimID;
    }

    /**
     * Returns the voter ID
     * @return the voter ID
     */
    public int getVoterID() {
        return voterID;
    }

    /**
     * Returns the victim ID
     * @return the victim ID
     */
    public int getVictimID() {
        return victimID;
    }

    /**
     * Serializes the object
     *
     * @param writer the custom serialization writer
     * @throws java.io.IOException exception during writing
     *
     * @see com.topcoder.shared.netCommon.CSWriter
     * @see java.io.IOException
     */
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(voterID);
        writer.writeInt(victimID);
    }

    /**
     * Creates the object from a serialization stream
     *
     * @param writer the custom serialization reader
     * @throws java.io.IOException           exception during reading
     * @throws ObjectStreamException exception during reading
     *
     * @see com.topcoder.netCommon.CSWriter
     * @see java.io.IOException
     * @see java.io.ObjectStreamException
     */
    public void customReadObject(CSReader reader) throws IOException {
        voterID = reader.readInt();
        victimID = reader.readInt();
    }

    /**
     * Gets the string representation of this object
     *
     * @returns the string representation of this object
     */
    public String toString() {
        return "(WeakestLinkVote)[" + voterID + ", " + victimID + "]";
    }
}

