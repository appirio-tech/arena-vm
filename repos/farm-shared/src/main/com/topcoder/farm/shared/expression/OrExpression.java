/*
 * OrExpression
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
 * Expression representing a Disjunction between two expressions
 * 
 * Uses lazy evaluation to obtain the result  
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class OrExpression implements Expression {
    private Expression expression1;
    private Expression expression2;
    
    public OrExpression() {
    }
    
    /**
     * Creates a new OrExpression.
     * 
     * @param expression1 The first evaluated argument of the disjunction
     * @param expression2 The second evaluated argument of the disjunction
     */
    public OrExpression(Expression expression1, Expression expression2) {
        this.expression1 = expression1;
        this.expression2 = expression2;
    }

    /**
     * @see Expression#eval(Map)
     */
    public boolean eval(Map properties) {
        return expression1.eval(properties) || expression2.eval(properties);
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

    public void setExpression2(Expression op2) {
        this.expression2 = op2;
    }
    
    public String toString() {
        return "("+ expression1 +") OR ("+ expression2+")";
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
