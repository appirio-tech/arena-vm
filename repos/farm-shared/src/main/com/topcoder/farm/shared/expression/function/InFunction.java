/*
 * InFunction
 *
 * Created 07/28/2006
 */
package com.topcoder.farm.shared.expression.function;

import java.util.Collection;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.ResolvedCustomSerializable;

/**
 * InFunction represents a function that
 * returns true if value2, which must implement Collection interface,
 * contains value1
 *
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class InFunction implements BinaryFunction, ResolvedCustomSerializable {
    /**
     * An unique instance of this function
     */
    public static final InFunction INSTANCE = new InFunction();

    public InFunction() {
    }

    public boolean eval(Object value1, Object value2) {
        return value2 != null && (((Collection) value2).contains(value1));
    }

    /**
     * Only one instance is needed
     */
    public Object readResolve () {
        return INSTANCE;
    }

    public String toString() {
        return "IN";
    }

    public void customReadObject(CSReader cs) {
    }

    public void customWriteObject(CSWriter cs) {
    }
}