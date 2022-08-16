/*
 * ProcessorInvocationResponse
 * 
 * Created 07/03/2006
 */
package com.topcoder.farm.processor.api;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

/**
 * @author Diego Belfer (mural)
 * @version $Id: ProcessorInvocationResponse.java 52649 2006-10-05 21:50:50Z mural $
 */
public class ProcessorInvocationFeedback implements Serializable, CustomSerializable {
    private static final long serialVersionUID = 1L;

    /**
     * The internal farm request id, which this response belongs to 
     */
    private Long id;
   
    /**
     * The invocation feedback that will be sent to the client 
     */
    private Object feedback;

    public ProcessorInvocationFeedback() {
    }
    
    public ProcessorInvocationFeedback(Long id, Object feedback) {
        this.id =  id;
        this.feedback = feedback;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Object getFeedback() {
        return feedback;
    }

    public void setFeedback(Object feedback) {
        this.feedback = feedback;
    }

    
    public void customReadObject(CSReader cs) throws IOException, ObjectStreamException {
        this.id = new Long(cs.readLong());
        this.feedback = cs.readObject();
    }

    public void customWriteObject(CSWriter cs) throws IOException {
        cs.writeLong(this.id.longValue());
        cs.writeObject(this.feedback);
    }
}
