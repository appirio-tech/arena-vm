package com.topcoder.shared.problem;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;

import org.apache.log4j.Category;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

/**
 * This is a utility class for representing messages generate during problem statement parsing and construction. It
 * provides levels of meaning, a message, and, if possible, the location within the problem statement that the message
 * is related to.
 * 
 * @see com.topcoder.server.common.problem.ProblemStatementFactory
 * @author Qi Liu
 * @version $Id: ProblemMessage.java 71771 2008-07-18 05:34:07Z qliu $
 */
public class ProblemMessage implements Serializable, Cloneable, CustomSerializable {
    /** Specified that a message's level is ``warning'' */
    static public short WARNING = 0;

    /** Specified that a message's level is ``error'' */
    static public short ERROR = 1;

    /** Specified that a message's level is ``fatal error'' */
    static public short FATAL_ERROR = 2;

    /** Represents the level of the message. */
    private int type;

    /** Represents the line number of the message. */
    private int line;

    /** Represents the column number of the message. */
    private int column;

    /** Represents the message. */
    private String message;

    /**
     * Creates a new instance of <code>ProblemMessage</code>. It is required by custom serialization.
     */
    public ProblemMessage() {
    }

    /**
     * Constructs a message with a known location.
     * 
     * @param type One of <code>WARNING</code>, <code>ERROR</code>, or <code>FATAL_ERROR</code>
     * @param message The content of the message
     * @param line The number of the line to which the message corresponds. Lines are counted starting at <code>1</code>.
     * @param column The number of the column within the line to which the message corresponds. Columns are counted
     *            starting at <code>1</code>.
     */
    public ProblemMessage(int type, String message, int line, int column) {
        if (message == null)
            message = "";
        this.type = type;
        this.message = message;
        this.line = line;
        this.column = column;
    }

    /**
     * Constructs a message with an unknown location.
     * 
     * @param type One of <code>WARNING</code>, <code>ERROR</code>, or <code>FATAL_ERROR</code>
     * @param message The content of the message
     */
    public ProblemMessage(int type, String message) {
        if (message == null)
            message = "";
        this.type = type;
        this.message = message;
        line = column = 0;
    }

    /**
     * The type of a message is one of <code>WARNING</code>, <code>ERROR</code>, or <code>FATAL_ERROR</code>.
     * 
     * @return The type of the message
     */
    public int getType() {
        return type;
    }

    /**
     * Returns the line in the problem statement XML to which the message corresponds (if known).
     * 
     * @return Either <code>0</code> if the line is unknown, or else a value greater than <code>0</code>.
     */
    public int getLine() {
        return line;
    }

    /**
     * Returns the column in the problem statement XML to which the message corresponds (if known).
     * 
     * @return Either <code>0</code> if the column is unknown, or else a value greater than <code>0</code>.
     */
    public int getColumn() {
        return column;
    }

    /**
     * Returns the content of the message.
     * 
     * @return the content of the message.
     */
    public String getMessage() {
        return message;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(type);
        writer.writeInt(line);
        writer.writeInt(column);
        writer.writeString(message);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        type = reader.readInt();
        line = reader.readInt();
        column = reader.readInt();
        message = reader.readString();
    }

    /**
     * Sends the message in an appropriate format to the appropriate log.
     * 
     * @param trace A <code>log4j.Category</code> (probably should be a <code>Logger</code> instead)
     */
    public void log(Category trace) {
        String text = toString();

        if (type == WARNING)
            trace.warn(text);
        else if (type == ERROR)
            trace.error(text);
        else if (type == FATAL_ERROR)
            trace.fatal(text);
    }

    /**
     * Converts the message and all information associated with it to a human-readable string.
     */
    public String toString() {
        StringBuffer buf = new StringBuffer(message.length() + 32);

        if (line != 0) {
            buf.append("Line ");
            buf.append(line);
            buf.append(": ");
        }
        if (column != 0) {
            buf.append("Column ");
            buf.append(column);
            buf.append(": ");
        }
        buf.append(message);
        return buf.toString();
    }
}
