/**
 * PhaseChange.java
 *
 * Description:		Specifies a phase change
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.netCommon.spectatorMessages;

//import com.topcoder.netCommon.*;

import com.topcoder.shared.netCommon.messages.*;
import com.topcoder.shared.netCommon.*;

import java.io.*;


public class PhaseChange extends Message {

    /** the phase identifier */
    private int phaseID;

    /** the time (in seconds) that are allocated to the phase */
    private int timeAllocated;

    /** Start of Contest */
    public final static int STARTCONTEST = 1;

    /** Registration phase  */
    public final static int REGISTRATION = 2;

    /** Coding phase  */
    public final static int CODING = 3;

    /** Intermission phase  */
    public final static int INTERMISSION = 4;

    /** Challenge phase  */
    public final static int CHALLENGE = 5;

    /** Voting phase (for weakest link rounds only) */
    public final static int VOTING = 6;

    /** Tiebreak voting phase (for weakest link rounds only) */
    public final static int TIEBREAKVOTING = 7;

    /** System Testing phase  */
    public final static int SYSTEMTEST = 8;

    /** End of Contest  */
    public final static int ENDCONTEST = 9;

    /** Constant indicating the MODERATED_CHATTING_PHASE */
    public static final int MODERATED_CHAT = 10;

    /**
     * No-arg constructor needed by customserialization
     *
     */
    public PhaseChange() {
    }

    /**
     * Constructs a phase change
     *
     * @param newPhaseID    the new phase identifier
     * @param timeAllocated the time allocated (in seconds) to the phase
     */
    public PhaseChange(int newPhaseID, int timeAllocated) {
        super();
        this.phaseID = newPhaseID;
        this.timeAllocated = timeAllocated;
    }


    /**
     * Returns the new phase
     * @return the new phase identifier
     */
    public int getPhaseID() {
        return phaseID;
    }

    /**
     * Returns the time (in seconds) allocated to the phase
     * @return the time (in seconds) allocated to the phase
     */
    public int getTimeAllocated() {
        return timeAllocated;
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
        super.customWriteObject(writer);
        writer.writeInt(phaseID);
        writer.writeInt(timeAllocated);
    }

    /**
     * Creates the object from a serialization stream
     *
     * @param reader the custom serialization reader
     * @throws java.io.IOException           exception during reading
     * @throws java.io.ObjectStreamException exception during reading
     *
     * @see com.topcoder.shared.netCommon.CSWriter
     * @see java.io.IOException
     * @see java.io.ObjectStreamException
     */
    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        phaseID = reader.readInt();
        timeAllocated = reader.readInt();
    }

    /**
     * Gets the string representation of this object
     *
     * @return the string representation of this object
     */
    public String toString() {
        return new StringBuffer().append("(PhaseChange)[").append(phaseID).append(", ").append(timeAllocated).append("]").toString();
    }

}


/* @(#)PhaseChange.java */
