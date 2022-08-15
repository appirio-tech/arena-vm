/*
 * ProcessorInvocationRequest
 * 
 * Created 07/03/2006
 */
package com.topcoder.farm.processor.api;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;

import com.topcoder.farm.shared.invocation.Invocation;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ProcessorInvocationRequest implements Serializable, CustomSerializable {
    private Long id;
    private Invocation invocation;
    private int requiredResources;
    
    public ProcessorInvocationRequest() {
        
    }
    
    public ProcessorInvocationRequest(Long id, Invocation invocation, int requiredResources) {
        this.id = id;
        this.invocation = invocation;
        this.requiredResources = requiredResources;
    }
    
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Invocation getInvocation() {
        return invocation;
    }
    public void setInvocation(Invocation invocation) {
        this.invocation = invocation;
    }
    public int getRequiredResources() {
        return requiredResources;
    }
    public void setRequiredResources(int requiredResources) {
        this.requiredResources = requiredResources;
    }
    
    
    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customReadObject(com.topcoder.shared.netCommon.CSReader)
     */
    public void customReadObject(CSReader cs) throws IOException, ObjectStreamException {
        this.id = new Long(cs.readLong());
        this.invocation = (Invocation) cs.readObject();
        this.requiredResources = cs.readInt();
    }

    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customWriteObject(com.topcoder.shared.netCommon.CSWriter)
     */
    public void customWriteObject(CSWriter cs) throws IOException {
        cs.writeLong(this.id.longValue());
        cs.writeObject(this.invocation);
        cs.writeInt(this.requiredResources);
    }
}
