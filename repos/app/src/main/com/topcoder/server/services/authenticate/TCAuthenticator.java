/*
 * Copyright (C) - 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.server.services.authenticate;

import com.topcoder.server.common.User;
import com.topcoder.server.ejb.DBServices.DBServices;
import com.topcoder.shared.util.logging.Logger;

/**
 * The oracle authenticator.
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
public class TCAuthenticator implements Authenticator {

    private static Logger s_trace = Logger.getLogger(TCAuthenticator.class);
    public User authenticateUser(DBServices s_dbServices, String username, String password, String newHandle)
            throws InvalidPasswordException, HandleTakenException {
        try {
            User user = s_dbServices.authenticateUser(username, password);
            //s_trace.debug("TCAUTH. handle:" + username + " pass:" + password);
            if (user == null) throw new InvalidPasswordException();
            return user;
        } catch (Exception e) {
            //s_trace.error("TCauthenticateUser", e);
            throw new InvalidPasswordException();
        }
    }
}
