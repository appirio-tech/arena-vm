/*
 * InvocationHeaderTO
 * 
 * Created 08/01/2006
 */
package com.topcoder.farm.controller.model;

import java.util.Date;

import com.topcoder.farm.shared.invocation.InvocationRequirements;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class InvocationHeaderTO {
    private Long id;
    private Date receivedDate;
    private Date dropDate;
    private int  priority;
    private int  assignAttempts;
    
    private InvocationRequirements requirements;
    
    public InvocationHeaderTO(){
    }
    
    public InvocationHeaderTO(Long id, Date receivedDate, Date dropDate, int priority, int assignAttempts, InvocationRequirements requirements) {
        this.id = id;
        this.receivedDate = receivedDate;
        this.dropDate = dropDate;
        this.priority = priority;
        this.requirements = requirements;
        this.assignAttempts = assignAttempts;
    }
    
    public InvocationRequirements getRequirements() {
        return requirements;
    }
    public void setRequirements(InvocationRequirements requiremens) {
        this.requirements = requiremens;
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
    public int getAssignAttempts() {
        return assignAttempts;
    }
    public void setAssignAttempts(int assignAttempts) {
        this.assignAttempts = assignAttempts;
    }
}
