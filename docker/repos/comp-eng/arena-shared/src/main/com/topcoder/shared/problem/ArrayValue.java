package com.topcoder.shared.problem;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.util.ArrayList;

/**
 * Defines a sub-class of <code>DataValue</code> which represents an array. The encoded format is '{element1,
 * element2, ..., }'.
 * 
 * @author Qi Liu
 * @version $Id: ArrayValue.java 71757 2008-07-17 09:13:19Z qliu $
 */
public class ArrayValue extends DataValue {
    /** Represents the list storing the values in the array. */
    private ArrayList values;

    /**
     * Creates a new instance of <code>ArrayValue</code>. It is required by custom serialization.
     */
    public ArrayValue() {
    }

    /**
     * Creates a new instance of <code>ArrayValue</code>. The array is given.
     * 
     * @param values the array to be held by this instance.
     */
    public ArrayValue(ArrayList values) {
        this.values = values;
    }

    /**
     * Creates a new instance of <code>ArrayValue</code>. The holding array is read from the data value reader. The
     * array will be with the given data type.
     * 
     * @param reader the data value reader to read the elements in the array.
     * @param type the type of the array.
     * @throws IOException if an I/O error occurs when reading the array elements.
     * @throws DataValueParseException if the array elements cannot be parsed from the reader.
     */
    public ArrayValue(DataValueReader reader, DataType type) throws IOException, DataValueParseException {
        parse(reader, type);
    }

    public void parse(DataValueReader reader, DataType type) throws IOException, DataValueParseException {
        try {
            reader.expect('{', true);

            DataType subtype = type.reduceDimension();

            values = new ArrayList();
            if (reader.checkAhead('}', true))
                return;
            do {
                values.add(DataValue.parseValue(reader, subtype));
            } while (reader.checkAhead(',', true));
            reader.expect('}', true);
        } catch (InvalidTypeException ex) {
            reader.exception("Invalid array type: " + type.getDescription());
        }
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeArrayList(values);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        values = reader.readArrayList();
    }

    public String encode() {
        String[] vals = new String[values.size()];
        int len = 2;

        for (int i = 0; i < vals.length; i++) {
            vals[i] = ((DataValue) values.get(i)).encode();
            len += vals[i].length() + 2;
        }

        StringBuffer buf = new StringBuffer(len);

        buf.append('{');
        for (int i = 0; i < vals.length; i++) {
            if (i > 0)
                buf.append(", ");
            buf.append(vals[i]);
        }
        buf.append(" }");
        return buf.toString();
    }

    public Object getValue() {
        return values.toArray();
    }
}
