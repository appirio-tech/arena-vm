package com.topcoder.shared.problem;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.io.StringReader;

/**
 * Defines a text reader which wraps a reader and contains helper functions to be used by data value parsers.
 * 
 * @author Qi Liu
 * @version $Id: DataValueReader.java 71757 2008-07-17 09:13:19Z qliu $
 */
public class DataValueReader {
    /** Represents the wrapped reader which can push back read characters. */
    private PushbackReader reader;

    /** Represents the lines, columns and previous read columns in the content. */
    private int line, column, prevcolumn = -1;

    /** Represents a flag indicating if the content is all read. */
    private boolean eof = false;

    /**
     * Creates a new instance of <code>DataValueReader</code>. The text to be read is given.
     * 
     * @param text the text to be read.
     * @throws IOException if an I/O error occurs.
     */
    public DataValueReader(String text) throws IOException {
        this(text, 1, 1);
    }

    /**
     * Creates a new instance of <code>DataValueReader</code>. The text to be read is given. The initial line and
     * column numbers are also given.
     * 
     * @param text the text to be read.
     * @param line the initial line number.
     * @param column the initial column number.
     * @throws IOException if an I/O error occurs.
     */
    public DataValueReader(String text, int line, int column) throws IOException {
        this(new StringReader(text), line, column);
    }

    /**
     * Creates a new instance of <code>DataValueReader</code>. The text is read via the given reader.
     * 
     * @param reader the reader to access the text.
     * @throws IOException if an I/O error occurs.
     */
    public DataValueReader(Reader reader) throws IOException {
        this(reader, 1, 1);
    }

    /**
     * Creates a new instance of <code>DataValueReader</code>. The text is read via the given reader. The initial
     * line and column numbers are also given.
     * 
     * @param reader the reader to access the text.
     * @param line the initial line number.
     * @param column the initial column number.
     * @throws IOException if an I/O error occurs.
     */
    public DataValueReader(Reader reader, int line, int column) throws IOException {
        this.reader = new PushbackReader(reader);
        this.line = line;
        this.column = column;
    }

    /**
     * Throws a <code>DataValueParseException</code>. The error message is given. The current line number and column
     * number are recorded.
     * 
     * @param message the error message.
     * @throws DataValueParseException always throw this error with the message, line number and colume number.
     */
    public void exception(String message) throws DataValueParseException {
        throw new DataValueParseException(message, line, column);
    }

    /**
     * Reads the next character. If it reaches EOF, -1 is returned.
     * 
     * @return the next character in the reader.
     * @throws IOException if an I/O error occurs.
     * @throws DataValueParseException if parsing the data value fails.
     */
    public int read() throws IOException, DataValueParseException {
        return read(false);
    }

    /**
     * Increases the column number by 1.
     */
    void incrementColumn() {
        column++;
    }

    /**
     * Increases the line number by 1. The column number is set as 1, after the value is stored as the previous column
     * number.
     */
    void incrementLine() {
        line++;
        prevcolumn = column;
        column = 1;
    }

    /**
     * Decreases the column number by 1. It correctly handles the column number if backtracing only one line.
     */
    void decrementColumn() {
        if (column < 2) {
            column = prevcolumn;
            prevcolumn = -1;
        } else
            column--;
    }

    /**
     * Reads the next character. Depending on the flag given, when reaching the EOF, either -1 is returned or exception is raised.
     * 
     * @param errorOnEOF a flag indicating whether to raise exception when reaching EOF.
     * @return the next character.
     * @throws IOException if an I/O error occurs.
     * @throws DataValueParseException if reaching EOF and the raising error flag is set.
     */
    public int read(boolean errorOnEOF) throws IOException, DataValueParseException {
        if (eof) {
            if (errorOnEOF)
                exception("Unexpected EOF");
            return -1;
        }

        int i = reader.read();

        if (i == -1) {
            eof = true;
            if (errorOnEOF)
                exception("Unexpected EOF");
            incrementColumn();
            return -1;
        }
        if ((char) i == '\n')
            incrementLine();
        else
            incrementColumn();
        return i;
    }

    /**
     * Puts back a character into the buffer.
     * 
     * @param c the character to be put back.
     * @throws IOException if an I/O error occurs.
     */
    public void unread(int c) throws IOException {
        decrementColumn();
        if (eof)
            eof = false;
        if (c != -1)
            reader.unread(c);
    }

    /**
     * Skips the next whitespace characters in the text.
     *  
     * @throws IOException if an I/O error occurs.
     * @throws DataValueParseException if parsing the data value fails.
     */
    public void skipWhitespace() throws IOException, DataValueParseException {
        int i = read();

        while (i != -1 && Character.isWhitespace((char) i))
            i = read();
        unread(i);
    }

    /**
     * Asserts the next character to be the given character.
     * 
     * @param c the expected next character in the text.
     * @throws IOException if an I/O error occurs.
     * @throws DataValueParseException if the next character is not the given character.
     */
    void expect(char c) throws IOException, DataValueParseException {
        expect(c, false);
    }

    /**
     * Asserts the next character to be the given character. Whitespaces may be skipped before assertion.
     * 
     * @param c the expected next character in the text.
     * @param whitespace a flag indicating if whitespaces should be skipped before assertion.
     * @throws IOException if an I/O error occurs.
     * @throws DataValueParseException if the next character is not the given character.
     */
    void expect(char c, boolean whitespace) throws IOException, DataValueParseException {
        if (whitespace)
            skipWhitespace();

        int i = read();

        if (i == -1) {
            unread(i);
            expectedException(c, "EOF");
        }
        if ((char) i != c) {
            unread(i);
            expectedException(c, i);
        }
    }

    /**
     * Throws an error when expecting one character but got another one.
     * 
     * @param x the expected character.
     * @param y the actual character.
     * @throws DataValueParseException always throw this error with description.
     */
    void expectedException(int x, int y) throws DataValueParseException {
        if (x == -1)
            expectedException("EOF", y);
        if (y == -1)
            expectedException(x, "EOF");
        expectedException("``" + (char) x + "''", "``" + (char) y + "''");
    }

    /**
     * Throws an error when expecting one character but got another one.
     * 
     * @param x the expected character in string description.
     * @param y the actual character.
     * @throws DataValueParseException always throw this error with description.
     */
    void expectedException(String x, int y) throws DataValueParseException {
        if (y == -1)
            expectedException(x, "EOF");
        expectedException(x, "``" + (char) y + "''");
    }

    /**
     * Throws an error when expecting one character but got another one.
     * 
     * @param x the expected character.
     * @param y the actual character in string description.
     * @throws DataValueParseException always throw this error with description.
     */
    void expectedException(int x, String y) throws DataValueParseException {
        if (x == -1)
            expectedException("EOF", y);
        expectedException("``" + (char) x + "''", y);
    }

    /**
     * Throws an error when expecting one character but got another one.
     * 
     * @param x the expected character in string description.
     * @param y the actual character in string description.
     * @throws DataValueParseException always throw this error with description.
     */
    void expectedException(String x, String y) throws DataValueParseException {
        exception("Expected " + x + ", got " + y);
    }

    /**
     * Gets a flag indicating if the next character is the given character.
     * 
     * @param c the character to be checked.
     * @return <code>true</code> if the next character is the given one; <code>false</code> otherwise.
     * @throws IOException if an I/O error occurs.
     * @throws DataValueParseException if parsing the data value fails.
     */
    boolean checkAhead(char c) throws IOException, DataValueParseException {
        return checkAhead(c, false);
    }

    /**
     * Gets a flag indicating if the next character is the given character. Whitespaces may be skipped before checking.
     * 
     * @param c the character to be checked.
     * @param whitespace a flag indicating if whitespaces should be skipped before checking.
     * @return <code>true</code> if the next character is the given one; <code>false</code> otherwise.
     * @throws IOException if an I/O error occurs.
     * @throws DataValueParseException if parsing the data value fails.
     */
    boolean checkAhead(char c, boolean whitespace) throws IOException, DataValueParseException {
        if (whitespace)
            skipWhitespace();

        int i = read();

        if ((char) i == c)
            return true;
        unread(i);
        return false;
    }
}
