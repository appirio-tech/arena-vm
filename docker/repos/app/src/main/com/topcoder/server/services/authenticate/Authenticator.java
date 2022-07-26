/*
* Copyright (C) - 2014 TopCoder Inc., All Rights Reserved.
*/
package com.topcoder.server.services.authenticate;

import com.topcoder.server.common.User;
import com.topcoder.server.ejb.DBServices.DBServices;

/**
 * The authenticator interface.
 *
 * <p>
 * Changes in version 1.1 (TopCoder Competition Engine - Revise Authentication Logic for SSO v1.0):
 * <ol>
 *      <li>Removed {@link #authenticateUser(DBServices, String)} method.</li>
 * </ol>
 * </p>
 *
 * @author TCSASSEMBLER
 * @version 1.1
 */
public interface Authenticator {
    User authenticateUser(DBServices s_dbServices, String username, String password, String newHandle)
            throws InvalidPasswordException, HandleTakenException;
}
