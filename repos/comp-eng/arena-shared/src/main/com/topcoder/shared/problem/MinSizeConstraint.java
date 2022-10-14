package com.topcoder.shared.problem;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

import java.io.IOException;
import java.io.ObjectStreamException;

/**
 * The <code>MinSizeConstraint</code> class is for constraints related to the minimum size (length) of a parameter.
 * 
 * @author mitalub
 * @version $Id: MinSizeConstraint.java 71757 2008-07-17 09:13:19Z qliu $
 */
public class MinSizeConstraint extends SizeConstraint {
    /**
     * Creates a new instance of <code>MinSizeConstraint</code>. The minimum size, dimension and the parameter name
     * are given.
     * 
     * @param size the minimum size of the constraint.
     * @param dimension the dimension of the constraint.
     * @param paramName the parameter name to be applied.
     */
    public MinSizeConstraint(int size, int dimension, String paramName) {
        super(size, dimension, paramName);
    }

    /**
     * Creates a new instance of <code>MinSizeConstraint</code>. It is required by custom serialization.
     */
    public MinSizeConstraint() {
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
