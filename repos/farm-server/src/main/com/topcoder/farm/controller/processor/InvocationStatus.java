/*
 * InvocationStatus
 * 
 * Created 11/17/2006
 */
package com.topcoder.farm.controller.processor;

import java.io.Serializable;
import java.util.Date;

/**
 * Invocation status holds Status information for an Invocation.
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class InvocationStatus implements Serializable {
    /**
     * The unique id of the InvocationData.
     */
    private Long id;
    /**
     * Timestamp in which the invocation was received by a controller
     */
    private Date receivedDate;
    /**
     * Timestamp indicating when this invocation should be discarded.
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

    public InvocationStatus() {
        
    }
    
    public InvocationStatus(Long id, Date receivedDate, Date dropDate,
            Date assignDate, long assignationTtl, String assignedProcessor,
            int priority, int requiredResources, String clientName,
            String clientRequestId) {
        this.id = id;
        this.receivedDate = receivedDate;
        this.dropDate = dropDate;
        this.assignDate = assignDate;
        this.assignationTtl = assignationTtl;
        this.assignedProcessor = assignedProcessor;
        this.priority = priority;
        this.requiredResources = requiredResources;
        this.clientName = clientName;
        this.clientRequestId = clientRequestId;
    }

    public long getAssignationTtl() {
        return assignationTtl;
    }

    public void setAssignationTtl(long assignationTtl) {
        this.assignationTtl = assignationTtl;
    }

    public Date getAssignDate() {
        return assignDate;
    }

    public void setAssignDate(Date assignDate) {
        this.assignDate = assignDate;
    }

    public String getAssignedProcessor() {
        return assignedProcessor;
    }

    public void setAssignedProcessor(String assignedProcessor) {
        this.assignedProcessor = assignedProcessor;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientRequestId() {
        return clientRequestId;
    }

    public void setClientRequestId(String clientRequestId) {
        this.clientRequestId = clientRequestId;
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

    public int getRequiredResources() {
        return requiredResources;
    }

    public void setRequiredResources(int requiredResources) {
        this.requiredResources = requiredResources;
    }
}
