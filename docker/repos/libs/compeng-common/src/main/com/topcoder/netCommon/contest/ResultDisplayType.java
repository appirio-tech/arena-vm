/*
 * ResultDisplayType Created 09/21/2007
 */
package com.topcoder.netCommon.contest;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.ResolvedCustomSerializable;

/**
 * Defines an enumeration which represents the information of a solution to be displayed in a summary.
 * 
 * @author Diego Belfer (mural)
 * @version $Id: ResultDisplayType.java 72046 2008-07-31 06:47:43Z qliu $
 */
public class ResultDisplayType implements Serializable, ResolvedCustomSerializable {
    /**
     * Represents the map of the IDs and enumeration objects.
     */
    private static final Map all = new HashMap(5);

    /**
     * Represents the enumeration which the points should be displayed in summary.
     */
    public static final ResultDisplayType POINTS = new ResultDisplayType(1, "Points");

    /**
     * Represents the enumeration which the system test results should be displayed in summary.
     */
    public static final ResultDisplayType PASSED_TESTS = new ResultDisplayType(2, "Passed Tests");

    /**
     * Represents the enumeration which the status should be displayed in summary.
     */
    public static final ResultDisplayType STATUS = new ResultDisplayType(3, "Status");

    private int id;

    private transient String description;

    /**
     * Creates a new instance of <code>ResultDisplayType</code>. It is required by custom serialization.
     */
    public ResultDisplayType() {
    }

    private ResultDisplayType(int id, String description) {
        this.id = id;
        this.description = description;
        ResultDisplayType.all.put(new Integer(id), this);
    }

    /**
     * Gets the ID of the enumeration.
     * 
     * @return the ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the description of the enumeration.
     * 
     * @return the description.
     */
    public String getDescription() {
        return description;
    }

    public int hashCode() {
        return id;
    }

    public boolean equals(Object obj) {
        return obj != null
            && (obj == this || (this.getClass().equals(obj.getClass()) && this.id == ((ResultDisplayType) obj).id));
    }

    public Object readResolve() {
        return get(id);
    }

    /**
     * Gets the singleton instance of the enumeration with the given ID.
     * 
     * @param id the ID of the enumeration.
     * @return the singleton instance of the enumeration.
     */
    public static ResultDisplayType get(int id) {
        ResultDisplayType value = (ResultDisplayType) all.get(new Integer(id));
        if (value == null)
            throw new IllegalArgumentException("Invalid id " + id + " for type " + ResultDisplayType.class.getName());
        return value;
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        this.id = reader.readInt();
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(this.id);
    }
}
