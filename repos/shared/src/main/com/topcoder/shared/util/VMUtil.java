/*
 * VMUtil
 * 
 * Created 07/30/2007
 */
package com.topcoder.shared.util;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class VMUtil {
    /**
     * Contains the unique ID for the current VM instance. This value should be unique between different applications and
     * even between different instances of the same application
     */
    public static String VM_INSTANCE_ID = System.getProperty("VM_INSTANCE_ID");
    
    /**
     * Returns the VM Identifier defined as System property when launching the current VM. (VM_INSTANCE_ID)
     * 
     * @return the id
     * @throws IllegalStateException if the property was not defined
     */
    public static String getVMInstanceId() {
        if (VM_INSTANCE_ID == null) {
            throw new IllegalStateException("You must define the System property VM_INSTANCE_ID before calling this method");
        }
        return VM_INSTANCE_ID;
    }
    
}
