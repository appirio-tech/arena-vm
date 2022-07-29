package com.topcoder.client.mpsqasApplet.common;

import com.topcoder.shared.problem.ValidValuesConstraint;
import com.topcoder.shared.problem.Range;
import com.topcoder.shared.problem.Value;

import java.util.ArrayList;

/**
 * An extension of a ValidValuesConstraint which allows the max size to be
 * set after the objects initialization and contains a method to set and
 * get text prompting the user for to enter the constraint.
 *
 * @author mitalub
 */
public class OpenValidValuesConstraint extends ValidValuesConstraint {

    public OpenValidValuesConstraint(int dimension, String paramName) {
        super(null, dimension, paramName);
    }

    public OpenValidValuesConstraint(ArrayList validValues, int dimension,
            String paramName) {
        super(validValues, dimension, paramName);
    }

    public OpenValidValuesConstraint() {
    }

    public void setValidValues(java.util.ArrayList validValues) {
        this.validValues = validValues;
    }

    public String getPromptString() {
        StringBuffer sb = new StringBuffer(50);
        for (int i = 0; i < dimension; i++) {
            if (i == 0) {
                sb.append("Elements of ");
            } else {
                sb.append("elements of ");
            }
        }
        sb.append(paramName);
        sb.append(" valid values: ");
        return sb.toString();
    }

    public String getInputString() {
        if (validValues == null) return "";
        StringBuffer sb = new StringBuffer(20);
        for (int i = 0; i < validValues.size(); i++) {
            if (validValues.get(i) instanceof Value) {
                sb.append(((Value) validValues.get(i)).getValue());
            } else if (validValues.get(i) instanceof Range) {
                sb.append(((Range) validValues.get(i)).getMin());
                sb.append("-");
                sb.append(((Range) validValues.get(i)).getMax());
            }
            if (i < validValues.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
}
