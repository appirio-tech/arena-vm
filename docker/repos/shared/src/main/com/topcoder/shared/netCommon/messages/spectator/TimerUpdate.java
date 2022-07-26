/**
 * TimerUpdate.java
 *
 * Description:		Specifies the time left in the current phase
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.shared.netCommon.messages.spectator;

import com.topcoder.shared.netCommon.messages.*;
import com.topcoder.shared.netCommon.*;

import java.io.*;


public class TimerUpdate extends Message {

    /** Time left (in seconds) */
    private int timeLeft;

    /**
     * No-arg constructor needed by customserialization
     *
     */
    public TimerUpdate() {
    }

    /**
     *  Constructs a Timer Update message
     *
     *  @param timeLeft the time (in seconds) left in the match
     */
    public TimerUpdate(int timeLeft) {
        super();
        this.timeLeft = timeLeft;
    }


    /**
     * Gets the time left in the current phase
     * @returns the time left (in seconds) in the current phase
     */
    public int getTimeLeft() {
        return timeLeft;
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
        super.customWriteObject(writer);
        writer.writeInt(timeLeft);
    }

    /**
     * Creates the object from a serialization stream
     *
     * @param writer the custom serialization reader
     * @throws java.io.IOException           exception during reading
     * @throws java.io.ObjectStreamException exception during reading
     *
     * @see com.topcoder.netCommon.CSWriter
     * @see java.io.IOException
     * @see java.io.ObjectStreamException
     */
    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        timeLeft = reader.readInt();
    }

    /**
     * Gets the string representation of this object
     *
     * @returns the string representation of this object
     */
    public String toString() {
        return new StringBuffer().append("(TimerUpdate)[").append(timeLeft).append("]").toString();
    }

}


/* @(#)TimerUpdate.java */
