package com.topcoder.shared.problem;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.util.ArrayList;

/**
 * The <code>ValidValuesConstraint</code> is a constraint containing information as to the valid values of a
 * parameter. The values are described in <code>Value</code> or <code>Range</code> instances.
 * 
 * @author mitalub
 * @version $Id: ValidValuesConstraint.java 71757 2008-07-17 09:13:19Z qliu $
 */
public class ValidValuesConstraint extends Constraint {
    /** Represents a list of all valid values/ranges in the enumeration constraint. */
    protected ArrayList validValues;

    /** Represents the parameter name to be applied. */
    protected String paramName;

    /** Represents the dimension of the constraint. */
    protected int dimension;

    /**
     * Creates a new instance of <code>ValidValuesConstraint</code>. The valid values/ranges, parameter name and dimension
     * of the constraint are given.
     * 
     * @param validValues An ArrayList where each element is an instance of Value or Range, and the valid elements are
     *            those elements in any one of the Ranges or Values.
     * @param paramName The name of the parameter.
     * @param dimension The dimension this constraint refers to.
     */
    public ValidValuesConstraint(ArrayList validValues, int dimension, String paramName) {
        this.validValues = validValues;
        this.paramName = paramName;
        this.dimension = dimension;
    }

    /**
     * Creates a new instance of <code>ValidValuesConstraint</code>. It is required by custom serialization.
     */
    public ValidValuesConstraint() {
    }

    /**
     * Gets the list of valid values/ranges of the constraint. There is no copy.
     * 
     * @return the list of valid values/ranges of the constraint.
     */
    public ArrayList getValidValues() {
        return validValues;
    }

    /**
     * Gets the dimension of the constraint.
     * 
     * @return the dimension of the constraint.
     */
    public int getDimension() {
        return dimension;
    }

    /**
     * Gets the parameter name which the constraint is applied to.
     * 
     * @return the parameter name.
     */
    public String getParamName() {
        return paramName;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeArrayList(validValues);
        writer.writeString(paramName);
        writer.writeInt(dimension);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        validValues = reader.readArrayList();
        paramName = reader.readString();
        dimension = reader.readInt();
    }

    public String toXML() {
        StringBuffer sb = new StringBuffer();
        sb.append("<valid-values>");
        for (int i = 0; i < validValues.size(); i++) {
            sb.append(((Element) validValues.get(i)).toXML());
        }
        sb.append("</valid-values>");
        return sb.toString();
    }

}
