package com.topcoder.shared.problem;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

import java.io.IOException;
import java.io.ObjectStreamException;

/**
 * The <code>Constraint</code> class is intended to be a generalization of input constraints in problem statements.
 * 
 * @see UserConstraint
 * @see MinSizeConstraint
 * @see MaxSizeConstraint
 * @see ValidValuesConstraint
 * @see Element
 * @see com.topcoder.server.common.problem.ConstraintFactory
 * @author Logan Hanks
 * @version $Id: Constraint.java 71757 2008-07-17 09:13:19Z qliu $
 */
abstract public class Constraint extends BaseElement {
    /** Represents the parameter of the constraint. The usage of this parameter is depending on the sub-classes. */
    protected String param;

    /**
     * Creates a new instance of <code>Constraint</code>. It is required by custom serialization.
     */
    public Constraint() {
    }

    /**
     * Creates a new instance of <code>Constraint</code>. The parameter is given.
     * 
     * @param param the parameter of the constraint.
     */
    public Constraint(String param) {
        this.param = param;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeString(param);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        param = reader.readString();
    }

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}
}
