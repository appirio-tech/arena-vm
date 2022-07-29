/*
 * ExceptionData
 * 
 * Created 06/29/2006
 */
package com.topcoder.farm.shared.invocation;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

/**
 * ExceptionData contains information about the exception thrown during
 * execution of the invocation. 
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ExceptionData implements Serializable, CustomSerializable {
    private String exceptionString;
    private String exceptionStackTrace;
    
    public ExceptionData() {
    }
    
    public ExceptionData(Throwable e) {
        this.exceptionString = e.toString();
        this.exceptionStackTrace = getTraceString(e);
    }

    private String getTraceString(Throwable e) {
        StringWriter writer = new StringWriter();
        e.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }

    public String getExceptionString() {
        return exceptionString;
    }

    public void setExceptionString(String exceptionClassName) {
        this.exceptionString = exceptionClassName;
    }

    public String getExceptionStackTrace() {
        return exceptionStackTrace;
    }

    public void setExceptionStackTrace(String exceptionStactTrace) {
        this.exceptionStackTrace = exceptionStactTrace;
    }

    public String toString() {
        return "ExceptionString: "+exceptionString+"\nException StackTrace: "+exceptionStackTrace;
    }
    
    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customReadObject(com.topcoder.shared.netCommon.CSReader)
     */
    public void customReadObject(CSReader cs) throws IOException, ObjectStreamException {
        this.exceptionString = cs.readString();
        this.exceptionStackTrace = cs.readString();
    }

    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customWriteObject(com.topcoder.shared.netCommon.CSWriter)
     */
    public void customWriteObject(CSWriter cs) throws IOException {
        cs.writeString(this.exceptionString);
        cs.writeString(this.exceptionStackTrace);
    }

}
