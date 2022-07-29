/*
 * AndExpression
 * 
 * Created 07/27/2006
 */
package com.topcoder.farm.shared.expression;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.util.Map;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Expression representing Conjunction of two expressions
 * 
 * Uses lazy evaluation to obtain the result  
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class AndExpression implements Expression {
    private Expression expression1;
    private Expression expression2;
    
    public AndExpression() {
    }
    
    /**
     * Creates a new AndExpression.
     * 
     * @param expression1 The first evaluated argument of the conjunction
     * @param expression2 The second evaluated argument of the conjunction
     */
    public AndExpression(Expression expression1, Expression expression2) {
        this.expression1 = expression1;
        this.expression2 = expression2;
    }

    public boolean eval(Map properties) {
        return expression1.eval(properties) && expression2.eval(properties);
    }

    public Expression getExpression1() {
        return expression1;
    }

    public void setExpression1(Expression expression1) {
        this.expression1 = expression1;
    }

    public Expression getExpression2() {
        return expression2;
    }

    public void setExpression2(Expression expression2) {
        this.expression2 = expression2;
    }
    
    public String toString() {
        return "("+ expression1 +") AND ("+ expression2 +")";
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        expression1 = (Expression) reader.readObject();
        expression2 = (Expression) reader.readObject();
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeObject(expression1);
        writer.writeObject(expression2);
    }
}
