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
 * applying a BinaryFunction over two values retrieved from the 
 * properties Map. It uses provided property names to obtain those values
 * 
 * The argument order for evaluating the function is the following 
 *  First  Argument : properties.get(propertyName1)
 *  Second Argument : properties.get(propertyName2)
 *  
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class PropertyToPropertyExpression implements Expression {
    private String propertyName1;
    private String propertyName2;
    private BinaryFunction function;
    
    public PropertyToPropertyExpression() {
    }
   
    /**
     * Creates a new PropertyToPropertyExpression using the provided arguments
     * 
     * @param propertyName1 The property name used to extract the first argument
     * @param propertyName2 The property name used to extract the second argument
     * @param function The function to use
     */
    public PropertyToPropertyExpression(String propertyName1, String propertyName2, BinaryFunction function) {
        this.propertyName1 = propertyName1;
        this.propertyName2 = propertyName2;
        this.function = function;
    }

    public boolean eval(Map properties) {
        return function.eval(properties.get(propertyName1), properties.get(propertyName2)); 
    }

    public BinaryFunction getFunction() {
        return function;
    }

    public void setFunction(BinaryFunction function) {
        this.function = function;
    }

    public String getPropertyName1() {
        return propertyName1;
    }

    public void setPropertyName1(String propertyName1) {
        this.propertyName1 = propertyName1;
    }

    public String getPropertyName2() {
        return propertyName2;
    }

    public void setPropertyName2(String propertyName2) {
        this.propertyName2 = propertyName2;
    }
    
    public String toString() {
        return ""+ function + "(valueOf("+propertyName1+"),valueOf("+propertyName2+"))";
    }
    
    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        propertyName1 = reader.readString();
        propertyName2 = reader.readString();
        function = (BinaryFunction) reader.readObject();
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeString(propertyName1);
        writer.writeString(propertyName2);
        writer.writeObject(function);
    }
}
