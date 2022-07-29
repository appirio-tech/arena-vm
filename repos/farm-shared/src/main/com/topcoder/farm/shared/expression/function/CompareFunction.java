/*
 * CompareFunction
 * 
 * Created 07/28/2006
 */
package com.topcoder.farm.shared.expression.function;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.ResolvedCustomSerializable;


/**
 * CompareFunction provides factory methods to create
 * functions that uses the result of compareTo method
 * to determine the order between two elements
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class CompareFunction implements BinaryFunction, ResolvedCustomSerializable {
    private static final int GT_SIGN = 1;
    private static final int LT_SIGN = -1;
    
    /**
     * Returns CompareFunction that returns true if value1 > value2
     */
    public static final CompareFunction GT = new CompareFunction(GT_SIGN, 0, ">");
    /**
     * Returns CompareFunction that returns true if value1 < value2
     */
    public static final CompareFunction LT = new CompareFunction(LT_SIGN, 0, "<");
    /**
     * Returns CompareFunction that returns true if value1 >= value2
     */
    public static final CompareFunction GE = new CompareFunction(GT_SIGN, -1, ">=");
    /**
     * Returns CompareFunction that returns true if value1 <= value2
     */
    public static final CompareFunction LE = new CompareFunction(LT_SIGN, -1, "<=");

    /**
     * Sign expected for comparision
     */
    private int opSign = 0;

    /**
     * Minimun value that expected for comparision  
     */
    private int minVal = 0;
    
    
    private transient String symbol;
    
    public CompareFunction() {
    }
    
    protected CompareFunction(int opSign, int minVal, String symbol) {
        this.opSign = opSign;
        this.minVal = minVal;
        this.symbol = symbol;
    }

    public boolean eval(Object value1, Object value2) {
        return ((Comparable) value1).compareTo(value2) * opSign > minVal;
    }
    
    /**
     * Only one instance is needed
     */
    public Object readResolve () {
        if (opSign == GT_SIGN) {
            if (minVal == 0) {
                return GT;
            } else {
                return GE;
            }
        } else {
            if (minVal == 0) {
                return LT;
            } else {
                return LE;
            }
        }
    }
    
    public String toString() {
        return "OP["+symbol+"]";
    }
    
    public void customReadObject(CSReader cs) throws IOException, ObjectStreamException {
        opSign = cs.readByte();
        minVal = cs.readByte();
    }

    public void customWriteObject(CSWriter cs) throws IOException {
        cs.writeByte((byte) opSign);
        cs.writeByte((byte) minVal);
    }
}