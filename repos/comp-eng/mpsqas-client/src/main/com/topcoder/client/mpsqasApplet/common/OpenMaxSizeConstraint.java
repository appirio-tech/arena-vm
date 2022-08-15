package com.topcoder.client.mpsqasApplet.common;

import com.topcoder.shared.problem.MaxSizeConstraint;

/**
 * An extension of a MaxSizeConstraint which allows the max size to be
 * set after the objects initialization and contains a method to set and
 * get text prompting the user for to enter the constraint.
 *
 * @author mitalub
 */
public class OpenMaxSizeConstraint extends MaxSizeConstraint {

    public OpenMaxSizeConstraint(int dimension, String paramName) {
        super(-1, dimension, paramName);
    }

    public OpenMaxSizeConstraint(int size, int dimension, String paramName) {
        super(size, dimension, paramName);
    }

    public void setSize(int size) {
        this.size = size;
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
        sb.append(" max length: ");
        return sb.toString();
    }

    public String getInputString() {
        if (size == -1) return "";
        return new StringBuffer(2).append(size).toString();
    }
}
