/*
 * BinaryFunction
 * 
 * Created 07/28/2006
 */
package com.topcoder.farm.shared.expression.function;

import java.io.Serializable;

import com.topcoder.shared.netCommon.CustomSerializable;

/**
 * Represents an function made using to arguments
 * and that produce a boolean result
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public interface BinaryFunction extends Serializable, CustomSerializable {
    
    /**
     * Return the result of evaluating this function with the 
     * given arguments
     * 
     * @param value1 First argument
     * @param value2 Second argument
     * 
     * @return The result of the evaluation
     */
    public boolean eval(Object value1, Object value2);
}
