/*
 * AssignedInvocation
 * 
 * Created 09/25/2006
 */
package com.topcoder.farm.controller.services;

import com.topcoder.farm.shared.invocation.Invocation;

/**
 * Updates all 
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class AssignedInvocation {
    private Invocation invocation;
    private int requiredResources;
    
    public AssignedInvocation() {
    }
    
    public AssignedInvocation(Invocation invocation, int requiredResources) {
        this.invocation = invocation;
        this.requiredResources = requiredResources;
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
}
