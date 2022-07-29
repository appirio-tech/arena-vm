/*
 * ContainsFunction
 *
 * Created 07/28/2006
 */
package com.topcoder.farm.shared.expression.function;

import java.util.Collection;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.ResolvedCustomSerializable;

/**
 * ContainsFunction represents a function that
 * returns true if value1, which must implement Collection interface,
 * contains value2
 *
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ContainsFunction implements BinaryFunction, ResolvedCustomSerializable {
    /**
     * The unique instance of this function
     */
    public static final ContainsFunction INSTANCE = new ContainsFunction();

    public ContainsFunction() {
    }

    public boolean eval(Object value1, Object value2) {
        return value1 != null && ((Collection) value1).contains(value2);
    }

    /**
     * Only one instance is needed
     */
    public Object readResolve () {
        return INSTANCE;
    }

    public String toString() {
        return "CONTAINS";
    }

    public void customReadObject(CSReader cs) {
    }

    public void customWriteObject(CSWriter cs) {
    }

}