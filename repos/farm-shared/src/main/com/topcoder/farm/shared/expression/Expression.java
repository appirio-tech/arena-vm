/*
 * Expression
 * 
 * Created 07/27/2006
 */
package com.topcoder.farm.shared.expression;

import java.io.Serializable;
import java.util.Map;

import com.topcoder.shared.netCommon.CustomSerializable;

/**
 * An Expression represents an entity that can be evaluated 
 * using a set of values and as a result of such evaluation
 * produces a boolean result 
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public interface Expression extends Serializable, CustomSerializable {
    
    /**
     * Returns the result of evaluating this expression
     * against the properties Map
     * 
     * @param properties Map containing values
     * 
     * @return The result of the evaluation
     */
    public boolean eval(Map properties);
}
