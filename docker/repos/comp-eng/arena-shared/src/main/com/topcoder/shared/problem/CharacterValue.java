package com.topcoder.shared.problem;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

import java.io.IOException;
import java.io.ObjectStreamException;

/**
 * Defines a sub-class of <code>DataValue</code>, which represents a character. The encoded format is '&lt;char&gt;'. The
 * character can be escaped by '\' when necessary.
 * 
 * @author Qi Liu
 * @version $Id: CharacterValue.java 71757 2008-07-17 09:13:19Z qliu $
 */
public class CharacterValue extends DataValue {
    /** Represents the character value. */
    private char value;

    /**
     * Creates a new instance of <code>CharacterValue</code>. It is required by custom serialization.
     */
    public CharacterValue() {
    }

    /**
     * Creates a new instance of <code>CharacterValue</code>. The character value is given.
     * 
     * @param value the character value.
     */
    public CharacterValue(char value) {
        this.value = value;
    }

    public void parse(DataValueReader reader, DataType type) throws IOException, DataValueParseException {
        reader.expect('\'', true);

        int i = reader.read(true);

        switch ((char) i) {
        case '\'':
            reader.exception("Missing character");
        case '\\':
            value = (char) reader.read(true);
            break;
        default:
            value = (char) i;
        }
        reader.expect('\'', true);
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt((int) value);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        value = (char) reader.readInt();
    }

    public String encode() {
        if (value == '\\' || value == '\'')
            return "'\\" + value + "'";
        return "'" + value + "'";
    }

    /**
     * Gets the character value in this instance.
     * 
     * @return the character value in this instance.
     */
    public char getChar() {
        return value;
    }

    public Object getValue() {
        return new Character(value);
    }
}
