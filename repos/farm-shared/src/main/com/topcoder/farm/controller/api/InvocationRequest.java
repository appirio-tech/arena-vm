/*
 * InvocationRequest
 * 
 * Created 06/24/2006
 */
package com.topcoder.farm.controller.api;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.topcoder.farm.shared.invocation.Invocation;
import com.topcoder.farm.shared.invocation.InvocationRequirements;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;


/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class InvocationRequest implements Serializable, CustomSerializable {
    /**
     * This constant when set as value in requiredResources property
     * indicates that the request must be execute in a processor
     * without other task running simultaneously. 
     */
    public static final int EXCLUSIVE_PROC_USAGE = 0;

    /**
     * Indicates Normal priority for the request.<p>
     * @see ClientControllerNode#scheduleInvocation(String, InvocationRequest)
     */
    public static final int PRIORITY_NORMAL = 0;

    /**
     * Indicates High priority for the request.
     * @see ClientControllerNode#scheduleInvocation(String, InvocationRequest)
     */
    public static final int PRIORITY_HIGH = -100;

    /**
     * Indicates Low priority for the request.
     * @see ClientControllerNode#scheduleInvocation(String, InvocationRequest)
     */
    public static final int PRIORITY_LOW = +100;
    
    /**
     * This is the unique Id of the request assigned by the caller.
     * All operations regarding to request cancelation, query will be done
     * using this id or using a prefix for this id.
     */
    private String id;
    
    /**
     * Contains an object the that will be return to the client
     * when the response is notified. It should containg necessary information
     * to handle response properly
     */
    private Object attachment;
    
    /**
     * Requirements determining all requisites the target must accomplish
     * to execute this invocation.
     */
    private InvocationRequirements requirements;
    
    /**
     * A map<String, String> containing the name
     * of the property to set as the Key and the shared object
     * key as the value
     */
    private Map sharedObjectsToSet = new HashMap();
    
    /**
     * The invocation itself
     */
    private Invocation invocation;

    /**
     * This number indicates the required resources a processor must
     * have available to execute this invocation. 0 indicates all resources
     * available on the processor.
     */
    private int requiredResources = 1;
    
    /**
     * Indicates the priority of this invocation.<p>
     */
    private int priority = PRIORITY_NORMAL;
    
    public InvocationRequest() {
    }
    
    public InvocationRequest(String id, InvocationRequirements requirements, Invocation invocation) {
        this.id = id;
        this.requirements = requirements;
        this.invocation = invocation;
    }
    
    public InvocationRequest(String id, Object attachment, InvocationRequirements requirements, Invocation invocation) {
        this.id = id;
        this.attachment = attachment;
        this.requirements = requirements;
        this.invocation = invocation;
    }

    /**
     * @return Returns the id.
     */
    public String getId() {
        return id;
    }

    /**
     * @param id The id to set.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return Returns the invocation.
     */
    public Invocation getInvocation() {
        return invocation;
    }

    /**
     * @param invocation The invocation to set.
     */
    public void setInvocation(Invocation invocation) {
        this.invocation = invocation;
    }

    /**
     * @return Returns the requirements.
     */
    public InvocationRequirements getRequirements() {
        return requirements;
    }

    /**
     * @param requirements The requirements to set.
     */
    public void setRequirements(InvocationRequirements requirements) {
        this.requirements = requirements;
    }
    
    public void addSharedObjectRef(String propertyName, String objectKey) {
        sharedObjectsToSet.put(propertyName, objectKey);
    }
    
    public Map getSharedObjectRefs() {
        return Collections.unmodifiableMap(sharedObjectsToSet);
    }
    
    public Object getAttachment() {
        return attachment;
    }

    public void setAttachment(Object attachment) {
        this.attachment = attachment;
    }
    
    public int getRequiredResources() {
        return requiredResources;
    }

    public void setRequiredResources(int requiredResources) {
        if (requiredResources < 0) {
            throw new IllegalArgumentException("requiredResources should be >= 0");
        }
        this.requiredResources = requiredResources;
    }
    
    public void setRequestAsExclusiveProcessorUsage() {
        this.requiredResources = EXCLUSIVE_PROC_USAGE;
    }
    
    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    
    public String toString() {
        return "[" + id + ", " + invocation + "]";
    }
    
    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customReadObject(com.topcoder.shared.netCommon.CSReader)
     */
    public void customReadObject(CSReader cs) throws IOException, ObjectStreamException {
        this.id = cs.readString();
        this.attachment = cs.readObject();
        this.invocation = (Invocation) cs.readObject();
        this.requiredResources = cs.readInt();
        this.sharedObjectsToSet = cs.readHashMap();
        this.requirements = (InvocationRequirements) cs.readObject();
        this.priority = cs.readInt();
    }

    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customWriteObject(com.topcoder.shared.netCommon.CSWriter)
     */
    public void customWriteObject(CSWriter cs) throws IOException {
        cs.writeString(this.id);
        cs.writeObject(this.attachment);
        cs.writeObject(this.invocation);
        cs.writeInt(this.requiredResources);
        cs.writeMap(this.sharedObjectsToSet);
        cs.writeObject(this.requirements);
        cs.writeInt(priority);
    }


}
