/*
 * InvocationRequirements
 * 
 * Created 06/24/2006
 */
package com.topcoder.farm.shared.invocation;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;

import com.topcoder.farm.shared.expression.BooleanExpression;
import com.topcoder.farm.shared.expression.Expression;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class InvocationRequirements implements Serializable, CustomSerializable{
    private Expression filterExpression = BooleanExpression.TRUE;

    public InvocationRequirements() {
    }

    public InvocationRequirements(Expression filterExpression) {
        this.filterExpression = filterExpression;
    }
    
    public Expression getFilterExpression() {
        return filterExpression;
    }

    public void setFilterExpression(Expression filterExpression) {
        this.filterExpression = filterExpression;
    }
    
    public String toString() {
        return "{filterExpressions=["+filterExpression+"]}";
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        filterExpression = (Expression) reader.readObject();
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeObject(filterExpression);
    }
}
