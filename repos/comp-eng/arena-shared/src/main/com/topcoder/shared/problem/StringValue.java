package com.topcoder.shared.problem;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

import java.io.IOException;
import java.io.ObjectStreamException;

/**
 * Defines a sub-class of <code>DataValue</code> which represents a string. The encoded format is
 * &quot;&lt;string&gt;&quot;. The string can be escaped by '\' when necessary.
 * 
 * @author Qi Liu
 * @version $Id: StringValue.java 71757 2008-07-17 09:13:19Z qliu $
 */
public class StringValue extends DataValue {
    /** Represents the string stored in this instance. */
    private String value;

    /**
     * Creates a new instance of <code>StringValue</code>. It is required by custom serialization.
     */
    public StringValue() {
    }

    /**
     * Creates a new instance of <code>StringValue</code>. The string is given.
     * 
     * @param value the string to be held by this instance.
     */
    public StringValue(String value) {
        this.value = value;
    }

    public void parse(DataValueReader reader, DataType type) throws IOException, DataValueParseException {
        reader.expect('"', true);

        StringBuffer buf = new StringBuffer(64);

        while (!reader.checkAhead('"')) {
            int i = reader.read(true);
            char c;

            if ((char) i == '\\')
                c = (char) reader.read(true);
            else
                c = (char) i;
            buf.append(c);
        }
        value = buf.toString();
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeString(value);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        value = reader.readString();
    }

    public String encode() {
        StringBuffer buf = new StringBuffer(value.length() + 32);

        buf.append('"');
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);

            if (c == '\\' || c == '"')
                buf.append('\\');
            buf.append(c);
        }
        buf.append('"');
        return buf.toString();
    }

    public Object getValue() {
        return value;
    }
}
