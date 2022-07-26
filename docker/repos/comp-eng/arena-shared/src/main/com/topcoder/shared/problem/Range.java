package com.topcoder.shared.problem;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

import java.io.IOException;
import java.io.ObjectStreamException;

/**
 * A class to represent a range of values.
 * 
 * @author mitalub
 * @version $Id: Range.java 71757 2008-07-17 09:13:19Z qliu $
 */
public class Range extends BaseElement {
    /** Represents the minimum and maximum value of the range. */
    private String min, max;

    /**
     * Creates a new instance of <code>Range</code>. The minimum and maximum values of the range are given.
     * 
     * @param min the minimum value of the range.
     * @param max the maximum value of the range.
     */
    public Range(String min, String max) {
        this.min = min;
        this.max = max;
    }

    /**
     * Creates a new instance of <code>Range</code>. It is required by custom serialization.
     */
    public Range() {
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeString(min);
        writer.writeString(max);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        min = reader.readString();
        max = reader.readString();
    }

    /**
     * Gets the minimum value of the range.
     * 
     * @return the minimum value of the range.
     */
    public String getMin() {
        return min;
    }

    /**
     * Gets the maximum value of the range.
     * 
     * @return the maximum value of the range.
     */
    public String getMax() {
        return max;
    }

    public String toXML() {
        StringBuffer sb = new StringBuffer();
        sb.append("<range min='");
        sb.append(ProblemComponent.encodeHTML(min.toString()));
        sb.append("' max='");
        sb.append(ProblemComponent.encodeHTML(max.toString()));
        sb.append("'></range>");
        return sb.toString();
    }
}
