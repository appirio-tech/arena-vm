/**
 * ProblemData.java
 *
 * Description:		Structure containing information about a problem
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.shared.netCommon.messages.spectator;

import com.topcoder.shared.netCommon.*;

import java.io.*;


public class ProblemData implements Serializable, Cloneable, CustomSerializable {

    /** Identifier of the problem */
    private int problemID;

    /** Point value of the problem */
    private int pointValue;

    /**
     * No-arg constructor needed by customserialization
     *
     */
    public ProblemData() {
    }

    /**
     * Constructor of a ProblemData
     *
     * @param problemID  the identifier of a problem
     * @param pointValue the point value of a problem
     */
    public ProblemData(int problemID, int pointValue) {
        this.problemID = problemID;
        this.pointValue = pointValue;
    }

    /**
     * Gets the problemID
     *
     * @returns the unique identifier of the problem
     */
    public int getProblemID() {
        return problemID;
    }

    /**
     * Gets the point value
     *
     * @returns the point value of the problem
     */
    public int getPointValue() {
        return pointValue;
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
        writer.writeInt(problemID);
        writer.writeInt(pointValue);
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
        problemID = reader.readInt();
        pointValue = reader.readInt();
    }

    /**
     * Gets the string representation of this object
     *
     * @returns the string representation of this object
     */
    public String toString() {
        return new StringBuffer().append("(ProblemData)[").append(problemID).append(", ").append(pointValue).append("]").toString();
    }
}


/* @(#)ProblemData.java */
