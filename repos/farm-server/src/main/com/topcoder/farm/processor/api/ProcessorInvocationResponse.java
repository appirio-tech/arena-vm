/*
 * ProcessorInvocationResponse
 * 
 * Created 07/03/2006
 */
package com.topcoder.farm.processor.api;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;

import com.topcoder.farm.shared.invocation.InvocationResult;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ProcessorInvocationResponse implements Serializable, CustomSerializable {
    private static final long serialVersionUID = 1L;
    /**
     * The internal farm request id, which this response belongs to 
     */
    private Long id;
    /**
     * This property indicates the resources assigned to process this task.
     * This value could be obtained in the server, but for now it faster that 
     * processor resends this value than looking for it again
     */
    private int takenResources;
    
    /**
     * The invocation result that will be sent to the client 
     */
    private InvocationResult result;

    public ProcessorInvocationResponse() {
    }
    
    public ProcessorInvocationResponse(Long id, int takenResources, InvocationResult result) {
        this.id =  id;
        this.result = result;
        this.takenResources = takenResources;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public InvocationResult getResult() {
        return result;
    }

    public void setResult(InvocationResult result) {
        this.result = result;
    }

    public int getTakenResources() {
        return takenResources;
    }

    public void setTakenResources(int takenResources) {
        this.takenResources = takenResources;
    }
    
    
    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customReadObject(com.topcoder.shared.netCommon.CSReader)
     */
    public void customReadObject(CSReader cs) throws IOException, ObjectStreamException {
        this.id = new Long(cs.readLong());
        this.result = (InvocationResult) cs.readObject();
        this.takenResources = cs.readInt();
    }

    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customWriteObject(com.topcoder.shared.netCommon.CSWriter)
     */
    public void customWriteObject(CSWriter cs) throws IOException {
        cs.writeLong(this.id.longValue());
        cs.writeObject(this.result);
        cs.writeInt(this.takenResources);
    }
}
