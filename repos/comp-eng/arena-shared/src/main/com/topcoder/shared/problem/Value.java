package com.topcoder.shared.problem;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

import java.io.IOException;
import java.io.ObjectStreamException;

/**
 * A class to represent a value.
 * 
 * @author mitalub
 * @version $Id: Value.java 71757 2008-07-17 09:13:19Z qliu $
 */
public class Value extends BaseElement {
    /** Represents the string value to be held. */
    private String value;

    /**
     * Creates a new instance of <code>Value</code>. The string value to be held is given.
     * 
     * @param value the string value to be held.
     */
    public Value(String value) {
        this.value = value;
    }

    /**
     * Gets the string value held.
     * 
     * @return the string value held.
     */
    public String getValue() {
        return value;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeString(value);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        value = reader.readString();
    }

    public String toXML() {
        StringBuffer sb = new StringBuffer();
        sb.append("<value value='");
        sb.append(ProblemComponent.encodeHTML(value.toString()));
        sb.append("'></value>");
        return sb.toString();
    }
}
