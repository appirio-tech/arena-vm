/*
 * TCStaffPermission.java
 *
 * Created on July 12, 2005, 2:46 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.topcoder.server.AdminListener.security;

import com.topcoder.security.policy.GenericPermission;

/**
 *
 * @author rfairfax
 */
public class TCStaffPermission extends GenericPermission { 

    public final static String NAME = "TopCoder Staff";

    public TCStaffPermission() {
        super(NAME);
    }
    
}
