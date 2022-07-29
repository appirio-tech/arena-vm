/*
 * InvocationRequestRef
 * 
 * Created 09/21/2006
 */
package com.topcoder.farm.controller.api;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

/**
 * InvocationRequest reference object. It represents a reference
 * to an InvocationRequest made by the client.
 *  
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class InvocationRequestRef implements Serializable, CustomSerializable {
    
    /**
     * The request id
     */
    private String requestId;
    /**
     * The attachment object included in the request
     */
    private Object attachment;
    
    public InvocationRequestRef() {
    }
    
    public InvocationRequestRef(String requestId, Object attachment) {
        this.requestId = requestId;
        this.attachment = attachment;
    }

    public Object getAttachment() {
        return attachment;
    }
    
    public void setAttachment(Object attachment) {
        this.attachment = attachment;
    }
    
    public String getRequestId() {
        return requestId;
    }
    
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
    
    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customReadObject(com.topcoder.shared.netCommon.CSReader)
     */
    public void customReadObject(CSReader cs) throws IOException, ObjectStreamException {
        this.requestId = cs.readString();
        this.attachment = cs.readObject();
    }

    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customWriteObject(com.topcoder.shared.netCommon.CSWriter)
     */
    public void customWriteObject(CSWriter cs) throws IOException {
        cs.writeString(this.requestId);
        cs.writeObject(this.attachment);
    }
}
