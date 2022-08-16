package com.topcoder.server.AdminListener.security;

import java.io.Serializable;

import com.topcoder.security.policy.GenericPermission;

/**
 * A TCPermission to be granted to any user to provide access to specified 
 * round. The name of such permission is equal to String representation of
 * specified round ID that this permission grants access to prepended with
 * constant prefix.
 *
 * @author  TCSDESIGNER
 * @version 1.0 07/31/2003
 * @since   Admin Tool 2.0
 */
public class RoundAccessPermission extends GenericPermission { 

    /**
     * A String constant prefix that should be used to create a name of
     * this RoundAccessPermission in conjunction with round ID.
     */
    public final static String PREFIX = "Access To Round # ";

    /**
     * A String constant prefix that should be used to create a name of
     * this RoundAccessPermission in conjunction with round ID.
     */
    private int roundID;
    
    /**
     * Constructs new RoundAccessPermission to grant access to specified 
     * round. Given roundID is converted to String name of permission.
     *
     * @param roundID an ID of round to grant access to
     * @see   GenericPermission(String)
     * @see   PREFIX
     */
    public RoundAccessPermission(int roundID) {
        super(PREFIX + roundID);
        this.roundID = roundID;
    }

    /**
     * Gets the ID of round that this permission grants access to. Returned
     * value is obtained from conversion of this permission name to integer
     * value.
     *
     * @return an ID of round to grant access to
     * @see    TCPermission#getName()
     */
    public int getRoundID() {
        return roundID;
    }
}
