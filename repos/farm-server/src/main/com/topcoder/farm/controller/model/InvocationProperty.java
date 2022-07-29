/*
 * InvocationProperty
 * 
 * Created 09/20/2006
 */
package com.topcoder.farm.controller.model;


/**
 * InvocationProperty object represents a property that must be set to 
 * an Invocation prior to it be delivered to a processor.<p>
 * 
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class InvocationProperty {
    /**
     * Database id for the InvocationProperty object
     */
    private Long id;
    /**
     * The InvocationData containing the invocation that needs this property to be set.
     */
    private InvocationData invocation;
    /**
     * The name of the property to set 
     */
    private String propertyName;
    /**
     * The shared object used to set the value of the property
     */
    private SharedObject sharedObject;
    
    public InvocationProperty() {
    }
    
    public InvocationProperty(InvocationData invocation, String propertyName, SharedObject sharedObject) {
        this.invocation = invocation;
        this.propertyName = propertyName;
        this.sharedObject = sharedObject;
    }

    public InvocationData getInvocation() {
        return invocation;
    }
    protected void setInvocation(InvocationData invocation) {
        this.invocation = invocation;
    }
    public SharedObject getSharedObject() {
        return sharedObject;
    }
    public void setSharedObject(SharedObject object) {
        this.sharedObject = object;
    }
    public String getPropertyName() {
        return propertyName;
    }
    protected void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((invocation == null) ? 0 : invocation.hashCode());
        result = PRIME * result + ((propertyName == null) ? 0 : propertyName.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final InvocationProperty other = (InvocationProperty) obj;
        if (invocation == null) {
            if (other.invocation != null)
                return false;
        } else if (!invocation.equals(other.invocation))
            return false;
        if (propertyName == null) {
            if (other.propertyName != null)
                return false;
        } else if (!propertyName.equals(other.propertyName))
            return false;
        return true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
}
