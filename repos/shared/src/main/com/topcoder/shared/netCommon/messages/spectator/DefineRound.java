/**
 * DefineRound.java
 *
 * Description:		Defines the attributes of a round
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.shared.netCommon.messages.spectator;

import com.topcoder.shared.netCommon.messages.*;
import com.topcoder.shared.netCommon.*;

import java.io.*;

// Note: MUST define serializable for the announcer app...

public class DefineRound extends Message implements java.io.Serializable {

    /** Identifier of the round */
    private int roundID;

    /** Type of the round */
    private int roundType;

    /** Name of the round */
    private String roundName;

    /** Identifier of the contest the round is associated with */
    private int contestID;

    /**
     * No-arg constructor needed by customserialization
     *
     */
    public DefineRound() {
    }

    /**
     * Constructs a define room request
     *
     * @param roundID the identifier of the round
     * @param roundType the type of the round
     * @param roundName the name of the round
     * @param contestID the identifier of the contest the round is associated with
     */
    public DefineRound(int roundID, int roundType, String roundName, int contestID) {
        this.roundID = roundID;
        this.roundType = roundType;
        this.roundName = roundName;
        this.contestID = contestID;
    }

    /**
     * Returns the roundID.
     * @return int
     */
    public int getRoundID() {
        return roundID;
    }

    /**
     * Returns the round type.
     * @return int
     */
    public int getRoundType() {
        return roundType;
    }

    /**
     * Returns the roundName.
     * @return String
     */
    public String getRoundName() {
        return roundName;
    }


    /**
     * Returns the contestID.
     * @return int
     */
    public int getContestID() {
        return contestID;
    }

    /**
     * Serializes the object
     *
     * @param writer the custom serialization writer
     * @throws java.io.IOException exception during writing
     *
     * @see com.topcoder.netCommon.CSWriter
     * @see java.io.IOException
     */
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(roundID);
        writer.writeInt(roundType);
        writer.writeString(roundName);
        writer.writeInt(contestID);
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
        roundID = reader.readInt();
        roundType = reader.readInt();
        roundName = reader.readString();
        contestID = reader.readInt();
    }

    /**
     * Gets the string representation of this object
     *
     * @returns the string representation of this object
     */
    public String toString() {
        return "(DefineRound)[" + roundID + ", " + roundType + "," + roundName + ", " + contestID + "]";
    }

}


/* @(#)DefineRound */
