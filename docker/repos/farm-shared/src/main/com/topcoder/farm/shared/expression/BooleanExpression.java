/*
 * BooleanExpression
 * 
 * Created 08/01/2006
 */
package com.topcoder.farm.shared.expression;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.util.Map;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class BooleanExpression implements Expression {
    /**
     * Expression that evaluates always <code>true</code>
     */
    public static final BooleanExpression TRUE = new BooleanExpression(true);
    
    /**
     * Expression that evaluates always <code>false</code>
     */
    public static final BooleanExpression FALSE = new BooleanExpression(false);

    /**
     * Value to be returned
     */
    private boolean value;
    
    public BooleanExpression() {
    }
    
    /**
     * Creates a new BooleanExpression that will evaluate
     * always to <code>value</code>
     * 
     * @param value The value to evaluate to
     */
    public BooleanExpression(boolean value) {
        this.value = value;
    }

    public boolean eval(Map properties) {
        return value;
    }
    
    public String toString() {
        return ""+value;
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        value = reader.readBoolean();
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeBoolean(value);
    }
}
