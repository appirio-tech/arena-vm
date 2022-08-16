/*
 * InvocationData
 * 
 * Created 08/01/2006
 */
package com.topcoder.farm.controller.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.topcoder.farm.shared.invocation.Invocation;
import com.topcoder.farm.shared.invocation.InvocationRequirements;
import com.topcoder.farm.shared.invocation.InvocationResult;

/**
 * The InvocationData contains all information related to 
 * an invocation.
 *
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class InvocationData {
    /**
     * Constant containing the max time to live a request can have.
     * This is the default value: 365 days
     */
    public static final long MAX_TTL_TIME_MS = 31536000000L;
    
    /**
     * This is the max time allowed to a processor to notify for a result
     * before re-assigning the invocation to other processor. 
     */
    public static final int MAX_PROCESSING_TIME_MS = 300000;
    
    /**
     * Status an InvocationData could have
     */
    public static final int STATUS_PENDING   = 1;
    public static final int STATUS_ASSIGNED  = 2;
    public static final int STATUS_SOLVED    = 4;
    public static final int STATUS_NOTIFIED  = 8;
   
    /**
     * The unique id of the InvocationData.
     */
    private Long id;
    /**
     * Timestamp in which the invocation was received by a controller
     */
    private Date receivedDate;
    /**
     * Timestamp indicatin when this invocation should be discarded.
     */
    private Date dropDate;
    /**
     * Timestamp with the last time the invocation was assigned to a processor
     */
    private Date assignDate;
    /**
     * TimeToLive in ms after when the invocation should be re assigned if the previous assigned processor
     * did not notify a result
     */
    private long assignationTtl;
    /**
     * The name of the last processor assigned to the invocation
     */
    private String assignedProcessor; 
    /**
     * The number of times this invocation was assigned.
     */
    private int assignAttempts = 0;
    /**
     * Timestamp when the processor reported the result of this invocation
     */
    private Date solveDate;
    /**
     * Timestamp when the a controller notified the client with the result
     */
    private Date notifyDate;
    /**
     * Status of this invocation
     */
    private int  status;
    /**
     * Priority assigned when this invocation was received
     */
    private int  priority;

    /**
     * This number represents the required resources
     * to run this invocation on a processor.
     * A number equals {@link com.topcoder.farm.controller.api.InvocationRequest#EXCLUSIVE_PROC_USAGE} 
     * indicates the task requires all resources of the processor
     */
    private int requiredResources;
    /**
     * The name of the client making the request
     */
    private String clientName;
    
    /**
     * The id given by the client
     */
    private String clientRequestId;
    /**
     * An attachment that will be sent to the client along with 
     * the response
     */
    private Object clientAttachment;
    /**
     * Requirements a processor must meet to process
     * this invocation
     */
    private InvocationRequirements requirements;
    /**
     * The invocation object as sent by the client
     */
    private Invocation invocation; 
    /**
     * The invocation result if this invocation was processed
     */
    private InvocationResult result;
    /**
     * Contains a set of objects that should be set to the invocation before sending it 
     * to a processor.
     */
    private Set propertiesToSet = new HashSet();

    

    public InvocationData() {
    }
    
    /**
     * Sets the status as Pending and sets receivedDate and dropDate
     * for this InvocationData
     */
    public void setAsPending() {
        this.status = STATUS_PENDING;
        this.receivedDate = new Date();
    }
    
    /**
     * Sets the status as Assigned and sets assignDate
     * for this InvocationData
     */
    public void setAsAssigned() {
        this.status = STATUS_ASSIGNED;
        this.assignDate = new Date();
    }
    
    /**
     * Sets the status as SOLVED and sets the solveDate
     * for this InvocationData
     */
    public void setAsSolved() {
        this.status = STATUS_SOLVED;
        this.solveDate = new Date();
    }
    
    /**
     * Sets the status as NOTIFIED and sets the notifyDate
     * for this InvocationData
     */
    public void setAsNotified() {
        this.status = STATUS_NOTIFIED;
        this.notifyDate = new Date();
    }
    
    public InvocationRequirements getRequirements() {
        return requirements;
    }
    public void setRequirements(InvocationRequirements requiremens) {
        this.requirements = requiremens;
    }
    public Date getAssignDate() {
        return assignDate;
    }
    public void setAssignDate(Date assignDate) {
        this.assignDate = assignDate;
    }
    public String getClientName() {
        return clientName;
    }
    public void setClientName(String clientId) {
        this.clientName = clientId;
    }
    public String getClientRequestId() {
        return clientRequestId;
    }
    public void setClientRequestId(String clientRequestId) {
        this.clientRequestId = clientRequestId;
    }
    public int getRequiredResources() {
        return requiredResources;
    }
    public void setRequiredResources(int requiredResources) {
        this.requiredResources = requiredResources;
    }
    public long getAssignationTtl() {
        return assignationTtl;
    }
    public void setAssignationTtl(long ttlAssignation) {
        this.assignationTtl = ttlAssignation;
    }
    public Date getDropDate() {
        return dropDate;
    }
    public void setDropDate(Date dropDate) {
        this.dropDate = dropDate;
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
    public int getPriority() {
        return priority;
    }
    public void setPriority(int priority) {
        this.priority = priority;
    }
    public Date getReceivedDate() {
        return receivedDate;
    }
    public void setReceivedDate(Date receivedDate) {
        this.receivedDate = receivedDate;
    }
    public InvocationResult getResult() {
        return result;
    }
    public void setResult(InvocationResult result) {
        this.result = result;
    }
    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }

    public Date getNotifyDate() {
        return notifyDate;
    }

    public void setNotifyDate(Date notifyDate) {
        this.notifyDate = notifyDate;
    }

    public Date getSolveDate() {
        return solveDate;
    }

    public void setSolveDate(Date solveDate) {
        this.solveDate = solveDate;
    }


    public String getAssignedProcessor() {
        return assignedProcessor;
    }
    
    public int getAssignAttempts() {
        return assignAttempts;
    }
    
    public void setAssignAttempts(int assignAttempts) {
        this.assignAttempts = assignAttempts;
    }

    public void setAssignedProcessor(String assignedProcessor) {
        this.assignedProcessor = assignedProcessor;
    }
    
    public Set getPropertiesToSet() {
        return propertiesToSet;
    }

    public void setPropertiesToSet(Set propertiesToSet) {
        this.propertiesToSet = propertiesToSet;
    }

    public void addPropertyToSet(String propertyNameToSet, SharedObject object) {
        propertiesToSet.add(new InvocationProperty(this, propertyNameToSet, object));
    }

    public Object getClientAttachment() {
        return clientAttachment;
    }

    public void setClientAttachment(Object clientAttachment) {
        this.clientAttachment = clientAttachment;
    }

    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((clientName == null) ? 0 : clientName.hashCode());
        result = PRIME * result + ((clientRequestId == null) ? 0 : clientRequestId.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final InvocationData other = (InvocationData) obj;
        if (clientName == null) {
            if (other.clientName != null)
                return false;
        } else if (!clientName.equals(other.clientName))
            return false;
        if (clientRequestId == null) {
            if (other.clientRequestId != null)
                return false;
        } else if (!clientRequestId.equals(other.clientRequestId))
            return false;
        return true;
    }

}
