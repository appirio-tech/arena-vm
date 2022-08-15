/*
* Copyright (C) 2008 - 2014 TopCoder Inc., All Rights Reserved.
*/
package com.topcoder.server.services.authenticate;

import com.topcoder.security.TCSubject;
import com.topcoder.server.common.User;
import com.topcoder.server.ejb.DBServices.DBServices;
import com.topcoder.shared.util.logging.Logger;

/**
 * The all competitors OLTP authenticator.
 *
 * <p>
 * Changes in version 1.1 (TopCoder Competition Engine - Revise Authentication Logic for SSO v1.0):
 * <ol>
 *      <li>Removed {@link #authenticateUser(DBServices, String)} method.</li>
 * </ol>
 * </p>
 *
 * @autor Diego Belfer (Mural), TCSASSEMBLER
 * @version 1.1
 */
public class AllCompetitorsOLTPAuthenticator implements Authenticator {
    private static Logger log = Logger.getLogger(AllCompetitorsOLTPAuthenticator.class);

	//TODO Review this, it was extracted from the Verisign branch.. Need for it.
    public User authenticateUser(DBServices s_dbServices, String username, String password, String newHandle)
            throws InvalidPasswordException, HandleTakenException {
        try {
            TCSubject tcsubject = CommonOLTPAuthenticatorClient.authenticate(username, password);
            if (tcsubject == null) throw new InvalidPasswordException();

            log.info("User '" + username + "' is a valid user. Now checking for admin rights.");

            int userID = (int) tcsubject.getUserId();
            log.info("Here is the USER ID: "+userID);
            //this does admin auth for us
            User u = s_dbServices.getUser(userID);
            
            log.info("User found");
            
            u.setCompetitionUser(true);
            u.setHSCompetitionUser(false);

            return u;
        } catch (Exception e) {
            //e.printStackTrace();
            log.error("TCauthenticateUser", e);
            throw new InvalidPasswordException();
        }
    }
}