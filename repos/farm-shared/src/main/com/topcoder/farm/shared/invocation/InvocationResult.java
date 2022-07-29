/*
 * InvocationResult
 * 
 * Created 06/29/2006
 */
package com.topcoder.farm.shared.invocation;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class InvocationResult implements Serializable, CustomSerializable {
    private boolean exceptionThrown;
    private ExceptionData exceptionData;
    private Object returnValue;
    
    public InvocationResult() {
    }
    
    /**
     * @return Returns the exceptionThrown.
     */
    public boolean isExceptionThrown() {
        return exceptionThrown;
    }
    /**
     * @param exceptionThrown The exceptionThrown to set.
     */
    public void setExceptionThrown(boolean exceptionThrown) {
        this.exceptionThrown = exceptionThrown;
    }
    /**
     * @return Returns the returnValue.
     */
    public Object getReturnValue() {
        return returnValue;
    }
    /**
     * @param returnValue The returnValue to set.
     */
    public void setReturnValue(Object returnValue) {
        this.returnValue = returnValue;
    }
    /**
     * @return Returns the exceptionData.
     */
    public ExceptionData getExceptionData() {
        return exceptionData;
    }
    /**
     * @param exceptionData The exceptionData to set.
     */
    public void setExceptionData(ExceptionData exceptionData) {
        this.exceptionData = exceptionData;
    }
    
    public String toString() {
        String innerMsg = ""+(exceptionThrown ? exceptionData : returnValue);
        return "[ExceptionThrown=" + exceptionThrown + ", " + innerMsg.substring(0,  Math.min(innerMsg.length(), 30))+ "]";
    }

    
    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customReadObject(com.topcoder.shared.netCommon.CSReader)
     */
    public void customReadObject(CSReader cs) throws IOException, ObjectStreamException {
        this.exceptionThrown = cs.readBoolean();
        this.returnValue = cs.readObject();
        this.exceptionData = (ExceptionData) cs.readObject();
    }

    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customWriteObject(com.topcoder.shared.netCommon.CSWriter)
     */
    public void customWriteObject(CSWriter cs) throws IOException {
        cs.writeBoolean(this.exceptionThrown);
        cs.writeObject(this.returnValue);
        cs.writeObject(this.exceptionData);
    }
}
