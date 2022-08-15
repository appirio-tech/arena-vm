/*
 * RegExpMatchFunction
 *
 * Created 07/28/2006
 */
package com.topcoder.farm.shared.expression.function;

import java.util.regex.Pattern;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.ResolvedCustomSerializable;

/**
 * Function that evaluates if the value1 is matched by the Pattern
 * specified in value2
 *
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class RegExpMatchFunction implements BinaryFunction, ResolvedCustomSerializable {
    /**
     * The unique instance of this class
     */
    public static final RegExpMatchFunction INSTANCE = new RegExpMatchFunction();

    public RegExpMatchFunction() {
    }

    public boolean eval(Object value1, Object value2) {
        return value1!= null && value2 != null && (((Pattern) value2).matcher((CharSequence) value1).matches());
    }

    /**
     * Only one instance is needed
     */
    public Object readResolve () {
        return INSTANCE;
    }


    public String toString() {
        return "RegExp";
    }

    public void customReadObject(CSReader cs) {
    }

    public void customWriteObject(CSWriter cs) {
    }
}