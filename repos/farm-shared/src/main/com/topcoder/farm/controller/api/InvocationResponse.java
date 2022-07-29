/*
 * InvocationResponse
 * 
 * Created 06/29/2006
 */
package com.topcoder.farm.controller.api;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;

import com.topcoder.farm.shared.invocation.InvocationResult;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

/**
 * InvocationResponse  
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class InvocationResponse implements Serializable, CustomSerializable {
    private String requestId;
    private Object attachment;
    private InvocationResult result;
    
    public InvocationResponse() {
        
    }
    
    public InvocationResponse(String requestId, Object attachment, InvocationResult result) {
        this.requestId = requestId;
        this.attachment = attachment;
        this.result = result;
    }

    public String getRequestId() {
        return requestId;
    }
    
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
    
    public InvocationResult getResult() {
        return result;
    }
    
    public void setResult(InvocationResult result) {
        this.result = result;
    }

    public Object getAttachment() {
        return attachment;
    }

    public void setAttachment(Object attachment) {
        this.attachment = attachment;
    }
    
    public String toString() {
        return "Response[id="+requestId+"]";
    }
    
    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customReadObject(com.topcoder.shared.netCommon.CSReader)
     */
    public void customReadObject(CSReader cs) throws IOException, ObjectStreamException {
        this.requestId = cs.readString();
        this.attachment = cs.readObject();
        this.result = (InvocationResult) cs.readObject();
    }

    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customWriteObject(com.topcoder.shared.netCommon.CSWriter)
     */
    public void customWriteObject(CSWriter cs) throws IOException {
        cs.writeString(this.requestId);
        cs.writeObject(this.attachment);
        cs.writeObject(this.result);
    }
}
