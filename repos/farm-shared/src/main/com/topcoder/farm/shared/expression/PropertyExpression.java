/*
 * PropertyExpression
 * 
 * Created 07/27/2006
 */
package com.topcoder.farm.shared.expression;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.util.Map;

import com.topcoder.farm.shared.expression.function.BinaryFunction;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Expression that obtains the result of its evaluation 
 * applying a BinaryFunction on two values, the first one 
 * retrieved from the properties Map using the provided name, 
 * and the other one is used as provided.
 *
 * The argument order used for evaluating the function is the following 
 *  First  Argument : properties.get(propertyName)
 *  Second Argument : value
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class PropertyExpression implements Expression {
    private String propertyName;
    private Object value;
    private BinaryFunction function;
    
    public PropertyExpression() {
    }
    
    /**
     * Creates a new PropertyExpression using the provided arguments
     * 
     * @param propertyName The property name to extract first argument 
     * @param value The value used as second argument
     * @param function The function to use 
     */
    public PropertyExpression(String propertyName, Object value, BinaryFunction function) {
        this.propertyName = propertyName;
        this.value = value;
        this.function = function;
    }

    public boolean eval(Map properties) {
        return function.eval(properties.get(propertyName), value); 
    }
    
    public String toString() {
        return ""+ function + "(valueOf("+propertyName+")," + value + ")";
    }
    
    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        propertyName = reader.readString();
        value = reader.readObject();
        function = (BinaryFunction) reader.readObject();
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeString(propertyName);
        writer.writeObject(value);
        writer.writeObject(function);
    }
}
