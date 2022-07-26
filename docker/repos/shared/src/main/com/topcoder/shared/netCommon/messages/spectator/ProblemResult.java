/**
 * ProblemResult.java
 *
 * Description:		Specifies a problem result notification
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.shared.netCommon.messages.spectator;

import com.topcoder.shared.netCommon.*;

import java.io.*;


public class ProblemResult extends ProblemEvent {

    /** the type of result */
    private int result;

    /** the point value associated with the result */
    private double resultValue;
    
    /** challenge args */
    private Object[] args = new Object[0];

    /** was successful */
    public final static int SUCCESSFUL = 1;

    /** failed  */
    public final static int FAILED = 2;

    /** marathon problem is done being processed  */
    public final static int PROCESSED = 3;

    /**
     * No-arg constructor needed by customserialization
     *
     */
    public ProblemResult() {
    }

    /**
     * Constructs a problem event notification
     *
     * @param room             the room the event happened in
     * @param problemEventType the type of event happening to the problem
     * @param problemID        the unique identifier of the problem
     * @param problemWriter    the writer of the problem
     * @param sourceCoder      the coder generating the event (different than writer on a challenge)
     * @param timeLeft         the time left (in seconds) in the phase when the event occurred
     * @param result           the result of the event
     * @param resultValue      the value of the result (should always be a positive number)
     *
     * @see com.topcoder.netCommon.spectatorMessages.RoomData
     */
    public ProblemResult(RoomData room, int problemEventType, int problemID, String problemWriter, String sourceCoder, int timeLeft, int result, double resultValue) {
        super(room, problemEventType, problemID, problemWriter, sourceCoder, timeLeft);
        this.result = result;
        this.resultValue = resultValue;
    }
    
    public ProblemResult(RoomData room, int problemEventType, int problemID, String problemWriter, String sourceCoder, int timeLeft, int result, double resultValue, Object[] args) {
        super(room, problemEventType, problemID, problemWriter, sourceCoder, timeLeft);
        this.result = result;
        this.resultValue = resultValue;
        this.args = args;
    }
    
    public ProblemResult(RoomData room, int problemEventType, int problemID, String problemWriter, String sourceCoder, int timeLeft, int result, double resultValue, String text, int language, int submissionNumber) {
        this(room, problemEventType, problemID, problemWriter, sourceCoder, timeLeft,result, resultValue);
        this.language = language;
        this.programText = text;
        this.submissionNumber = submissionNumber;
    }
    
    public Object[] getArgs() {
        return args;
    }


    /**
     * Returns the result
     * @returns the result
     */
    public int getResult() {
        return result;
    }

    /**
     * Returns the value of the result
     * @returns the value of the result
     */
    public double getResultValue() {
        return resultValue;
    }

    /**
     * Serializes the object
     *
     * @param problemWriter the custom serialization problemWriter
     * @throws java.io.IOException exception during writing
     *
     * @see com.topcoder.netCommon.CSWriter
     * @see java.io.IOException
     */
    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeInt(result);
        writer.writeDouble(resultValue);
        writer.writeObjectArray(args);
    }

    /**
     * Creates the object from a serialization stream
     *
     * @param problemWriter the custom serialization reader
     * @throws java.io.IOException           exception during reading
     * @throws java.io.ObjectStreamException exception during reading
     *
     * @see com.topcoder.netCommon.CSWriter
     * @see java.io.IOException
     * @see java.io.ObjectStreamException
     */
    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        result = reader.readInt();
        resultValue = reader.readDouble();
        args = reader.readObjectArray();
    }

    /**
     * Gets the string representation of this object
     *
     * @returns the string representation of this object
     */
    public String toString() {
        return new StringBuffer().append("(ProblemResult)[").append(getRoom()).append(", ").append(getProblemEventType()).append(", ").append(getProblemID()).append(", ").append(getProblemWriter()).append(", ").append(getSourceCoder()).append(", ").append(result).append(", ").append(resultValue).append("]").toString();
    }

}


/* @(#)ProblemResult.java */
