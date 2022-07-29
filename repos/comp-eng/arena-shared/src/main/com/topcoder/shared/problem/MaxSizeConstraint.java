package com.topcoder.shared.problem;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

import java.io.IOException;
import java.io.ObjectStreamException;

/**
 * The <code>MaxSizeConstraint</code> class is for constraints related to the maximum size (length) of a parameter.
 * 
 * @author mitalub
 * @version $Id: MaxSizeConstraint.java 71757 2008-07-17 09:13:19Z qliu $
 */
public class MaxSizeConstraint extends SizeConstraint {
    /**
     * Creates a new instance of <code>MaxSizeConstraint</code>. The maximum size, dimension and the parameter name
     * are given.
     * 
     * @param size the maximum size of the constraint.
     * @param dimension the dimension of the constraint.
     * @param paramName the parameter name to be applied.
     */
    public MaxSizeConstraint(int size, int dimension, String paramName) {
        super(size, dimension, paramName);
    }

    /**
     * Creates a new instance of <code>MaxSizeConstraint</code>. It is required by custom serialization.
     */
    public MaxSizeConstraint() {
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
    }

    public String toXML() {
        // TODO left to logan to fill in to match the dtd...
        return "";
    }

}
