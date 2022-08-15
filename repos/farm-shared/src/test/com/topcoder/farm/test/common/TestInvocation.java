/*
 * TestInvocation
 * 
 * Created 06/29/2006
 */
package com.topcoder.farm.test.common;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.farm.shared.invocation.Invocation;
import com.topcoder.farm.shared.invocation.InvocationContext;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class TestInvocation implements Invocation {
    private String invocationId;
    private String clientId;
    private String threadName ;
    private String text;
    private int timeToWait;
    private RuntimeException exception;

    public TestInvocation() {
    }
    
    public TestInvocation(String invocationId , String clientId, String text, int timeToWait, RuntimeException exception) {
        super();
        this.invocationId = invocationId;
        this.clientId = clientId;
        this.threadName = Thread.currentThread().getName();
        this.text = text;
        this.timeToWait = timeToWait;
        this.exception = exception;
    }
    
    public Object run(InvocationContext context) {
        if (timeToWait > 0) {
            try {
                System.out.println("Sleepping invocation="+invocationId+" for="+timeToWait);
                Thread.sleep(timeToWait);
                System.out.println("Waiking up invocation="+invocationId);
            } catch (InterruptedException e) {
                throw (IllegalStateException) new IllegalStateException().initCause(e); 
            }
        }
        if (exception != null) {
            throw exception;
        }
        return this;
    }
    
    public String toString() {
        return "[invocationId=" + invocationId + ", clientId=" + clientId
                + ", threadIndex=" + threadName  + ",wait=" + timeToWait
                + ", text=" + text == null ? "null" : text.substring(Math.min(text.length(), 20))
                + "]";
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public RuntimeException getException() {
        return exception;
    }

    public void setException(RuntimeException exception) {
        this.exception = exception;
    }

    public String getInvocationId() {
        return invocationId;
    }

    public void setInvocationId(String invocationId) {
        this.invocationId = invocationId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public int getTimeToWait() {
        return timeToWait;
    }

    public void setTimeToWait(int timeToWait) {
        this.timeToWait = timeToWait;
    }

    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customReadObject(com.topcoder.shared.netCommon.CSReader)
     */
    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        this.invocationId = reader.readString();
        this.clientId = reader.readString();
        this.threadName = reader.readString();
        this.text = reader.readString();
        this.timeToWait = reader.readInt();
        this.exception = (RuntimeException) reader.readObject();;
        
    }

    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customWriteObject(com.topcoder.shared.netCommon.CSWriter)
     */
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeString(this.invocationId);
        writer.writeString(this.clientId);
        writer.writeString(this.threadName);
        writer.writeString(this.text);
        writer.writeInt(this.timeToWait);
        writer.writeObject(this.exception);
    }
}
