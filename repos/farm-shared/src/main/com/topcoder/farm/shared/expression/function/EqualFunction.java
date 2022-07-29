/*
 * EqualFunction
 *
 * Created 07/28/2006
 */
package com.topcoder.farm.shared.expression.function;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.ResolvedCustomSerializable;


/**
 * EqualFunction represents a function that
 * returns true if value1 is equal to value2
 *
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class EqualFunction implements BinaryFunction, ResolvedCustomSerializable {
    public static final EqualFunction INSTANCE = new EqualFunction();

    public boolean eval(Object value1, Object value2) {
        return (value1 == null && value2 == null) || (value1 != null && value1.equals(value2));
    }

    /**
     * Only one instance is needed
     */
    public Object readResolve () {
        return INSTANCE;
    }

    public String toString() {
        return "EQUALS";
    }


    public void customReadObject(CSReader cs) {
    }

    public void customWriteObject(CSWriter cs) {
    }
}