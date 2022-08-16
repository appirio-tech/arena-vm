/*
 * NotExpression
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
 * Expression that represents the Negation of another expression
 *  
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class NotExpression implements Expression {
    private Expression op1;
    
    public NotExpression() {
    }
    
    public NotExpression(Expression op1) {
        this.op1 = op1;
    }

    public boolean eval(Map properties) {
        return !op1.eval(properties);
    }

    public Expression getOp1() {
        return op1;
    }

    public void setOp1(Expression op1) {
        this.op1 = op1;
    }
    
    public String toString() {
        return "NOT ("+ op1 +")";
    }
    
    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        op1 = (Expression) reader.readObject();
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeObject(op1);
    }
}
