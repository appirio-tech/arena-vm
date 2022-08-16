/*
 * LongProblemEvent.java
 * 
 * Created on Jun 22, 2007, 11:16:39 AM
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.topcoder.shared.netCommon.messages.spectator;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import java.io.IOException;
import java.io.ObjectStreamException;

/**
 *
 * @author rfairfax
 */
public class LongProblemEvent extends RoomMessage {

    public LongProblemEvent() {
    }
    
    public LongProblemEvent(RoomData room, String writer, int problemID, int submissionCount, int submissionTime, int exampleCount, int exampleTime) {
        super(room);
        this.writer = writer;
        this.problemID = problemID;
        this.submissionCount = submissionCount;
        this.submissionTime = submissionTime;
        this.exampleCount = exampleCount;
        this.exampleTime = exampleTime;
    }
    
    protected String writer;
    protected int submissionCount;
    protected int exampleCount;
    protected int submissionTime;
    protected int exampleTime;
    
    /** the problem identifier */
    protected int problemID;
    
    public String getWriter() {
        return writer;
    }
    
    public int getProblemID() {
        return problemID;
    }
    
    public int getSubmissionCount() {
        return submissionCount;
    }
    
    public int getSubmissionTime() {
        return submissionTime;
    }
    
    public int getExampleCount() {
        return exampleCount;
    }
    
    public int getExampleTime() {
        return exampleTime;
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
        writer.writeString(this.writer);
        writer.writeInt(problemID);
        writer.writeInt(submissionCount);
        writer.writeInt(submissionTime);
        writer.writeInt(exampleCount);
        writer.writeInt(exampleTime);
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
        writer = reader.readString();
        problemID = reader.readInt();
        submissionCount = reader.readInt();
        submissionTime = reader.readInt();
        exampleCount = reader.readInt();
        exampleTime = reader.readInt();
    }
    
    public String toString() {
        return new StringBuffer().append("(LongProblemEvent)[").append(getRoom()).append(", ").append(submissionCount).append(", ").append(submissionTime).append(", ").append(exampleCount).append(", ").append(exampleTime).append("]").toString();
    }

}
