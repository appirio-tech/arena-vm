/**
 * PhaseChange.java
 *
 * Description:		Specifies a phase change
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.shared.netCommon.messages.spectator;

//import com.topcoder.netCommon.*;

import com.topcoder.shared.netCommon.messages.*;
import com.topcoder.shared.netCommon.*;

import java.io.*;


public class PhaseChange extends Message {

    /** the phase identifier */
    private int phaseID;

    /** the time (in seconds) that are allocated to the phase */
    private int timeAllocated;

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
