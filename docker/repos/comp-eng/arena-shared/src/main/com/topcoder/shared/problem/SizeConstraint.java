package com.topcoder.shared.problem;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

import java.io.IOException;
import java.io.ObjectStreamException;

/**
 * The <code>SizeConstraint</code> class is for constraints related to the size (length) of a parameter. The
 * SizeConstraint hold information as to which dimension the size refers to, the name of the parameter, and what the
 * size is.
 * 
 * @see MaxSizeConstraint
 * @see MinSizeConstraint
 * @author mitalub
 * @version $Id: SizeConstraint.java 71757 2008-07-17 09:13:19Z qliu $
 */
abstract public class SizeConstraint extends Constraint {
    /** Represents the size of the constraint. The actual meaning is defined by sub-classes. */
    protected int size;

    /** Represents the dimension of the constraint. */
    protected int dimension;

    /** Represents the parameter name to apply the constraint. */
    protected String paramName;

    /**
     * Creates a new instance of <code>SizeConstraint</code>. The size, dimension and the parameter name are
     * given.
     * 
     * @param size the size of the constraint.
     * @param dimension the dimension of the constraint.
     * @param paramName the parameter name to be applied.
     */
    public SizeConstraint(int size, int dimension, String paramName) {
        this.size = size;
        this.dimension = dimension;
        this.paramName = paramName;
    }

    /**
     * Creates a new instance of <code>SizeConstraint</code>. It is required by custom serialization.
     */
    public SizeConstraint() {
    }

    /**
     * Gets the size of the constraint.
     * 
     * @return the size of the constraint.
     */
    public int getSize() {
        return size;
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
     * Gets the parameter name of the constraint to be applied to.
     * 
     * @return the parameter name.
     */
    public String getParamName() {
        return paramName;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(size);
        writer.writeInt(dimension);
        writer.writeString(paramName);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        size = reader.readInt();
        dimension = reader.readInt();
        paramName = reader.readString();
    }
}
