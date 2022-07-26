package com.topcoder.shared.problem;

/**
 * Defines an exception which is thrown when parsing the encoded data value string failed.
 * 
 * @author Qi Liu
 * @version $Id: DataValueParseException.java 71757 2008-07-17 09:13:19Z qliu $
 */
public class DataValueParseException extends Exception {
    /** Represents the error message of this exception. */
    private String message;

    /** Represents the line number when the error occurs. */
    private int line;

    /** Represents the column number when the error occurs. */
    private int column;

    /**
     * Creates a new instance of <code>DataValueParseException</code>. The error message is given.
     * 
     * @param message the error message.
     */
    public DataValueParseException(String message) {
        this(message, 0, 0);
    }

    /**
     * Creates a new instance of <code>DataValueParseException</code>. The error message, and the location
     * of the error in the document are given.
     * @param message the error message.
     * @param line the line number of the error in the document.
     * @param column the column number of the error in the document.
     */
    public DataValueParseException(String message, int line, int column) {
        this.message = message;
        this.line = line;
        this.column = column;
    }

    public String getMessage() {
        return message;
    }

    /**
     * Gets the line number where the error occurs.
     * 
     * @return the line number of the error.
     */
    public int getLine() {
        return line;
    }

    /**
     * Gets the column number where the error occurs.
     * 
     * @return the column number of the error.
     */
    public int getColumn() {
        return column;
    }

    public String toString() {
        String result = "";

        if (line != 0)
            result += "Line " + line + ": ";
        if (column != 0)
            result += "Column " + column + ": ";
        return result + message;
    }
}
