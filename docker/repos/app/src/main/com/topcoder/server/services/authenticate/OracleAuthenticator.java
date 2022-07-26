/*
 * Copyright (C) - 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.server.services.authenticate;

import com.topcoder.server.common.ServerContestConstants;
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
public class OracleAuthenticator implements Authenticator {

    private static Logger s_trace = Logger.getLogger(OracleAuthenticator.class);

    public User authenticateUser(DBServices s_dbServices, String username, String password, String newHandle)
            throws InvalidPasswordException, HandleTakenException {
        String id = OracleAuthenticateClient.authenticate(username, password);
        if (id.equals("-1")) throw new InvalidPasswordException();
        try {
            if (newHandle != null && newHandle.trim().length() > 0) {
                //check to see that there isn't already a handle under this otn user
                User u = s_dbServices.getCompanyUser(ServerContestConstants.ORACLE_COMPANY_ID, id);
                if (u == null) {//hes not in yet, so create him
                    boolean taken = false;
                    taken = s_dbServices.checkTaken(newHandle);
                    if (taken) throw new HandleTakenException(newHandle);
                    u = s_dbServices.createUser(newHandle, id, ServerContestConstants.ORACLE_COMPANY_ID);
                }
                return u;
            } else {//not trying to create a new one
                User u = s_dbServices.getCompanyUser(ServerContestConstants.ORACLE_COMPANY_ID, id);
                if (u == null) throw new HandleTakenException(newHandle);
                return u;
            }
        } catch (Exception e) {
            s_trace.error("OracleAuthenticator", e);
        }
        return null;
    }

    public String toString() {
        return "OracleAuthenticateUser";
    }
}
