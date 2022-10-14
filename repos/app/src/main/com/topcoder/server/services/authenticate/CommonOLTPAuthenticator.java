/*
* Copyright (C) 2003 - 2014 TopCoder Inc., All Rights Reserved.
*/
package com.topcoder.server.services.authenticate;

import com.topcoder.server.common.User;
import com.topcoder.server.common.ServerContestConstants;
import com.topcoder.server.ejb.DBServices.DBServices;
import com.topcoder.security.TCSubject;
import com.topcoder.security.TCPrincipal;
import com.topcoder.server.common.Rating;
import com.topcoder.security.login.AuthenticationException;


import java.util.Iterator;

import com.topcoder.shared.util.logging.*;
import com.topcoder.shared.common.TCContext;

/**
 * The common OLTP authenticator..
 *
 * <p>
 * Changes in version 1.1 (TopCoder Competition Engine Arena Login Logic Update v1.0):
 * <ol>
 *      <li>Update {@link #authenticateUser(DBServices s_dbServices, String username,
 *            String password, String newHandle)} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.2 (TopCoder Competition Engine - Revise Authentication Logic for SSO v1.0):
 * <ol>
 *      <li>Removed {@link #authenticateUser(DBServices, String)} method.</li>
 * </ol>
 * </p>
 *
 * @author gtsipol, TCSASSEMBLER
 * @version 1.2
 */
public class CommonOLTPAuthenticator implements Authenticator {

    private static Logger log = Logger.getLogger(CommonOLTPAuthenticator.class);
    /**
     * @param s_dbServices the db service ejb entity.
     * @param username the user name to be authenticated.
     * @param password the password to be authenticated.
     * @param newHandle the user handle.
     * @throws InvalidPasswordException if the login failed with mismatch username and password
     * @throws HandleTakenException if the login communication has wrong exchange key data.
     */
    public User authenticateUser(DBServices s_dbServices, String username, String password, String newHandle)
            throws InvalidPasswordException, HandleTakenException {
        try {
            TCSubject tcsubject = CommonOLTPAuthenticatorClient.authenticate(username, password);
            if (tcsubject == null) throw new InvalidPasswordException();

            boolean isAdmin = false;
            boolean isWriterTester = false;

            log.info("User '" + username + "' is a valid user. Now checking for admin rights.");
            Iterator principals = tcsubject.getPrincipals().iterator();
            TCPrincipal principal;
            boolean valid = false;
            boolean competitionUser = false;
            boolean hsCompetitionUser = false;
            while (principals.hasNext()) {
                
                principal = (TCPrincipal) principals.next();
                log.debug(principal.getName()); 
                 if (principal.getName().equalsIgnoreCase(ServerContestConstants.GROUP_ADMIN)) {
                    log.info("User '" + username + "' is a valid admin.");
                    isAdmin = true;
                    valid = true;
               } else if (principal.getName().equalsIgnoreCase(ServerContestConstants.GROUP_WRITER_TESTER)) {
                    log.info("User '" + username + "' is a valid writer/tester.");
                    isAdmin = true;
                    isWriterTester = true;
                } else if (principal.getName().equalsIgnoreCase(ServerContestConstants.GROUP_STUDENT)) {
                    log.info("User '" + username + "' is a valid student.");
                } else if (principal.getName().equalsIgnoreCase(ServerContestConstants.GROUP_COMPETITION_USER)) {
                    log.info("User '" + username + "' is a competitor.");
                    valid = true;
                    competitionUser = true;
                } else if(principal.getName().equalsIgnoreCase(ServerContestConstants.GROUP_HS_COMPETITION_USER)) {
                    log.info("User '" + username + "' is a hs competitor.");
                    valid = true;
                    hsCompetitionUser = true;
                }

            }
            int userID = (int) tcsubject.getUserId();
            // add user group
            if (!valid) {
                log.info("Add '" + username + "' to table user_group_xref with ID = " + userID +" and group_id =" +
                        ServerContestConstants.CODER_GROUP_ID);
                valid = s_dbServices.addUserGroup(userID, ServerContestConstants.CODER_GROUP_ID);
            }
            
            // Clean up
            principals = null;
            principal = null;
            if(!valid) throw new InvalidPasswordException();
            
            log.info("Here is the USER ID: "+userID);

            // This checks if the user is valid or not.
            //this does admin auth for us
            User u = s_dbServices.getUser(userID, true);
            log.info("User found");
            
            if(u.getRating(Rating.ALGO).getRating() == -1) {
                //admin
                isAdmin = true;
            }
            
            //stop admins from registering
            if(isAdmin) {
                competitionUser = false;
                hsCompetitionUser = false;
            }
            
            u.setCompetitionUser(competitionUser);
            u.setHSCompetitionUser(hsCompetitionUser);

            //u.setLevelOneAdmin(isAdmin);
            //u.setLevelTwoAdmin(isWriterTester);
            log.debug("VALID 1");
            if (u == null) throw new InvalidPasswordException();
            log.debug("VALID 2");
            return u;
        } catch (InvalidPasswordException e) {
            // No need to print invalid password errors.
            throw e;
        } catch (AuthenticationException e) {
            // No need to print invalid password errors.
            throw new InvalidPasswordException();
        } catch (Exception e) {
            //e.printStackTrace();
            log.error("TCauthenticateUser", e);
            throw new InvalidPasswordException();
        }
    }
}
