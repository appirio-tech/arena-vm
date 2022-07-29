package com.topcoder.shared.problem;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

import java.io.IOException;
import java.io.ObjectStreamException;

/**
 * Defines a sub-class of <code>DataValue</code> which represents an integer (e.g. <code>short</code>). The integer
 * is always stored as <code>long</code>. The encoded format is the string representation of the number.
 * 
 * @author Qi Liu
 * @version $Id: IntegralValue.java 71757 2008-07-17 09:13:19Z qliu $
 */
public class IntegralValue extends DataValue {
    /** Represents the integer stored in this instance. */
    private long value;

    /**
     * Creates a new instance of <code>IntegralValue</code>. It is required by custom serialization.
     */
    public IntegralValue() {
    }

    /**
     * Creates a new instance of <code>IntegralValue</code>. The integer is given.
     * 
     * @param value the integer to be held by this instance.
     */
    public IntegralValue(long value) {
        this.value = value;
    }

    public void parse(DataValueReader reader, DataType type) throws IOException, DataValueParseException {
        reader.skipWhitespace();

        int i = reader.read();
        boolean valid = false;
        long sign = 1;

        value = 0;
        if ((char) i == '+' || (char) i == '-') {
            if ((char) i == '-')
                sign = -1;
            reader.skipWhitespace();
            i = reader.read();
        }
        while (i != -1 && Character.isDigit((char) i)) {
            value = value * 10 + (i - '0');
            i = reader.read();
            valid = true;
        }
        reader.unread(i);
        if (!valid)
            reader.expectedException("decimal digit", i);
        value *= sign;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeLong(value);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        value = reader.readLong();
    }

    public String encode() {
        return String.valueOf(value);
    }

    public Object getValue() {
        return new Long(value);
    }
}
