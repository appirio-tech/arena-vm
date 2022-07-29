package com.topcoder.shared.security;

/**
 * classes that implement Authorization will determine if a particular user has access to a particular resource
 * this could range from simple if "logged in then yes otherwise no" systems to full security systems with
 * permissions, roles groups etc.
 *
 * @author Greg Paul
 */
public interface Authorization {

    /** Can the user represented by this object access the specified resource? */
    public boolean hasPermission(Resource r) throws Exception;
}
