/**
 * ProblemEvent.java
 *
 * Description:		Specifies a problem event notification
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.shared.netCommon.messages.spectator;

import com.topcoder.shared.netCommon.*;

import java.io.*;


public class ProblemEvent extends RoomMessage {

    /** the type of event */
    private int problemEventType;

    /** the problem identifier */
    private int problemID;

    /** the time the event occurred */
    private int timeLeft;

    /** the problemWriter of the problem */
    private String problemWriter;

    /** the sourceCoder coder of the event */
    private String sourceCoder;
    
    /** Problem source */
    protected String programText;
    
    /** language used */
    protected int language;
    
    protected int submissionNumber;

    /** Problem being opened */
    public final static int OPENED = 1;

    /** Problem being closed  */
    public final static int CLOSED = 2;

    /** Problem being compiled */
    public final static int COMPILING = 3;

    /** Problem being tested */
    public final static int TESTING = 4;

    /** Problem being submitted */
    public final static int SUBMITTING = 5;

    /** Problem being challenged */
    public final static int CHALLENGING = 6;

    /** Problem being system tested */
    public final static int SYSTEMTESTING = 7;
    
    /** Saving the problem */
    public final static int SAVING = 8;

    /**
     * No-arg constructor needed by customserialization
     *
     */
    public ProblemEvent() {
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
     *
     * @see com.topcoder.netCommon.spectatorMessages.RoomData
     */
    public ProblemEvent(RoomData room, int problemEventType, int problemID, String problemWriter, String sourceCoder, int timeLeft) {
        super(room);
        this.problemEventType = problemEventType;
        this.problemID = problemID;
        this.problemWriter = problemWriter;
        this.sourceCoder = sourceCoder;
        this.timeLeft = timeLeft;
        this.programText = "";
    }
    
    public ProblemEvent(RoomData room, int problemEventType, int problemID, String problemWriter, String sourceCoder, int timeLeft, String programText, int language, int submissionNumber) {
        this(room, problemEventType, problemID, problemWriter, sourceCoder, timeLeft);
        this.programText = programText;
        this.language = language;
        this.submissionNumber = submissionNumber;
    }
    
    public int getLanguage() {
        return language;
    }
    
    public String getProgramText() {
        return programText;
    }
    
    public int getSubmissionNumber() {
        return submissionNumber;
    }

    /**
     * Returns the event type
     * @returns the event type
     */
    public int getProblemEventType() {
        return problemEventType;
    }

    /**
     * Returns the identifier of the problem
     * @returns the identifier of the problem
     */
    public int getProblemID() {
        return problemID;
    }

    /**
     * Returns the problemWriter of the problem
     * @returns the problemWriter of the problem
     */
    public String getProblemWriter() {
        return problemWriter;
    }

    /**
     * Returns the source coder of the event.  This will be different from the problemWriter on a challenge event (where the source coder is the coder challenging the problemWriter)
     * @returns the source coder of the event
     */
    public String getSourceCoder() {
        return sourceCoder;
    }

    /**
     * Returns the time (in seconds) that was left in the phase when the event occurred
     * @returns the time (in seconds)
     */
    public int getTimeLeft() {
        return timeLeft;
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
        writer.writeInt(problemEventType);
        writer.writeInt(problemID);
        writer.writeString(problemWriter);
        writer.writeString(sourceCoder);
        writer.writeInt(timeLeft);
        writer.writeString(programText);
        writer.writeInt(language);
        writer.writeInt(submissionNumber);
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
        problemEventType = reader.readInt();
        problemID = reader.readInt();
        problemWriter = reader.readString();
        sourceCoder = reader.readString();
        timeLeft = reader.readInt();
        programText = reader.readString();
        language = reader.readInt();
        submissionNumber = reader.readInt();
    }

    /**
     * Gets the string representation of this object
     *
     * @returns the string representation of this object
     */
    public String toString() {
        return new StringBuffer().append("(ProblemEvent)[").append(getRoom()).append(", ").append(problemEventType).append(", ").append(problemID).append(", ").append(problemWriter).append(", ").append(sourceCoder).append(", ").append(timeLeft).append("]").toString();
    }

}


/* @(#)ProblemEvent.java */
