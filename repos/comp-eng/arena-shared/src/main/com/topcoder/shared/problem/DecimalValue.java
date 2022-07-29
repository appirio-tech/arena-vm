package com.topcoder.shared.problem;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

import java.io.IOException;
import java.io.ObjectStreamException;

/**
 * Defines a sub-class of <code>DataValue</code> which represents a floating point number (e.g. <code>float</code>).
 * The floating point number is always stored as <code>double</code>. The encoded format is fixed or scientific
 * representation of the number.
 * 
 * @author Qi Liu
 * @version $Id: DecimalValue.java 71757 2008-07-17 09:13:19Z qliu $
 */
public class DecimalValue extends DataValue {
    /** Represents the floating point number stored in this instance. */
    private double value;

    /**
     * Creates a new instance of <code>DecimalValue</code>. It is required by custom serialization.
     */
    public DecimalValue() {
    }

    /**
     * Creates a new instance of <code>DecimalValue</code>. The floating point number is given.
     * 
     * @param value the floating point number to be held by this instance.
     */
    public DecimalValue(double value) {
        this.value = value;
    }

    public void parse(DataValueReader reader, DataType type) throws IOException, DataValueParseException {
        reader.skipWhitespace();

        int i = reader.read();
        long sign = 1;
        StringBuffer sb = new StringBuffer();

        value = 0;
        if ((char) i == '+' || (char) i == '-') {
            if ((char) i == '-')
                sign = -1;
            reader.skipWhitespace();
            i = reader.read();
        }

        int previousI = 0;

        while (i != -1
            && (Character.isDigit((char) i) || i == '.' || i == 'e' || i == 'E' || ((previousI == 'e' || previousI == 'E') && (i == '-' || i == '+')))) {
            previousI = i;
            sb.append((char) i);
            i = reader.read();
        }
        reader.unread(i);

        try {
            value = Double.parseDouble(sb.toString());
        } catch (Exception e) {
            reader.exception("Invalid decimal format.");
        }

        value *= sign;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeDouble(value);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        value = reader.readDouble();
    }

    public String encode() {
        return String.valueOf(value);
    }

    public Object getValue() {
        return new Double(value);
    }
}
