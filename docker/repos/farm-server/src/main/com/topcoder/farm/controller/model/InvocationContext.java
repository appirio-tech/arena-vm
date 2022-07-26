/*
 * InvocationContext
 * 
 * Created 09/25/2006
 */
package com.topcoder.farm.controller.model;

import java.util.Collection;

import com.topcoder.farm.shared.invocation.Invocation;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class InvocationContext {
    private Invocation invocation;
    private Collection<InvocationProperty> propertiesToSet;
    private int requiredResources;
    
    public InvocationContext() {
    }
    
    public InvocationContext(Invocation invocation, Collection<InvocationProperty> propertiesToSet, int requiredResources) {
        this.invocation = invocation;
        this.propertiesToSet = propertiesToSet;
        this.requiredResources = requiredResources;
    }

    public Collection<InvocationProperty> getPropertiesToSet() {
        return propertiesToSet;
    }
    public void setPropertiesToSet(Collection<InvocationProperty> propertiesToSet) {
        this.propertiesToSet = propertiesToSet;
    }
    public int getRequiredResources() {
        return requiredResources;
    }
    public void setRequiredResources(int requiredResources) {
        this.requiredResources = requiredResources;
    }
    public Invocation getInvocation() {
        return invocation;
    }
    public void setInvocation(Invocation invocation) {
        this.invocation = invocation;
    }

}
