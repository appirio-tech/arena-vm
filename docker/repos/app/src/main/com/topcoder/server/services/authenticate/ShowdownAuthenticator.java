/*
 * Copyright (C) 2008 - 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.server.services.authenticate;

import com.topcoder.server.common.Rating;
import com.topcoder.server.common.User;
import com.topcoder.server.ejb.DBServices.DBServices;
import com.topcoder.server.ejb.DBServices.DBServicesLocator;
import com.topcoder.shared.util.logging.Logger;


/**
 * Simple authenticator which does not relay on Standard authentication method.
 *
 * <p>
 * Changes in version 1.1 (TopCoder Competition Engine - Revise Authentication Logic for SSO v1.0):
 * <ol>
 *      <li>Removed {@link #authenticateUser(DBServices, String)} method.</li>
 * </ol>
 * </p>
 *
 * @author Diego Belfer (Mural), TCSASSEMBLER
 * @version 1.1
 */
public class ShowdownAuthenticator implements Authenticator {

    private static Logger log = Logger.getLogger(ShowdownAuthenticator.class);

    public User authenticateUser(DBServices s_dbServices, String username, String password, String newHandle)
            throws InvalidPasswordException, HandleTakenException {
        try {
            User simpleUser = DBServicesLocator.getService().authenticateUser(username, password);
            if (simpleUser == null) throw new InvalidPasswordException();

            boolean isAdmin = false;

            log.info("User '" + username + "' is a valid user. Now checking for admin rights.");

            // This checks if the user is valid or not.
            //this does admin auth for us
            User u = s_dbServices.getUser(simpleUser.getID(), true);
            log.info("User found");
            
            if(u.getRating(Rating.ALGO).getRating() == -1) {
                //admin
                isAdmin = true;
            }
            
            boolean competitionUser = true;
            //stop admins from registering
            if(isAdmin) {
                competitionUser = false;
            }
            u.setCompetitionUser(competitionUser);
            return u;
        } catch (InvalidPasswordException e) {
            // No need to print invalid password errors.
            throw e;
        } catch (Exception e) {
            log.error("TCauthenticateUser", e);
            throw new InvalidPasswordException();
        }
    }
}
