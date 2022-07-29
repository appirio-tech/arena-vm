/*
 * Copyright (C) 2006-2013 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.server.mpsqas.listener.impl;

import com.topcoder.netCommon.mpsqas.LookupValues;
import com.topcoder.netCommon.mpsqas.communication.message.LoginResponse;
import com.topcoder.server.ejb.MPSQASServices.MPSQASServices;

import java.util.ArrayList;
import java.rmi.RemoteException;

import org.apache.log4j.Logger;

/**
 * This is a bean responsible for authentication.
 *
 * <p>
 * <strong>Change log:</strong>
 * </p>
 *
 * <p>
 * Version 1.1 (Release Assembly - Dynamic Round Type List For Long and Individual Problems):
 * <ol>
 * <li>
 * Updated {@link #authenticate(String,String)} method to support lookup values.
 * </li>
 * </ol>
 * </p>
 *
 * <p>
 * <strong>Thread Safety: </strong><br/>
 * This class is immutable and thread-safe.
 * </p>
 *
 * @author Logan Hanks, gevak
 * @version 1.1
 */
public class BeanAuthenticator {

    MPSQASServices services;
    Logger logger;

    public BeanAuthenticator() {
        logger = Logger.getLogger(getClass());
        logger.info("BeanAuthenticator constructed");
    }

    public void setServices(MPSQASServices services) {
        this.services = services;
    }

    /**
     * Authenticates user.
     *
     * @param handle User handle.
     * @param passowrd User password.
     * @return Login response.
     */
    public LoginResponse authenticate(String handle, String password) {
        try {
            logger.info("Attempting to authenticate " + handle);

            ArrayList result = services.authenticateUser(handle, password);
            boolean success = ((Boolean) result.get(0)).booleanValue();

            if (success) {
                int id = ((Integer) result.get(1)).intValue();
                boolean admin = ((Boolean) result.get(2)).booleanValue();
                boolean writer = ((Boolean) result.get(3)).booleanValue();
                boolean tester = ((Boolean) result.get(4)).booleanValue();
                LookupValues lookupValues = services.getLookupValues();

                logger.info("Access granted: id=" + id + ", admin=" + admin + ", writer=" + writer + ", tester=" + tester);
                return LoginResponse.getSuccessfulLoginResponse(id, admin, writer, tester, lookupValues);
            } else {
                logger.info("Access denied: " + result.get(1));
                return LoginResponse.getFailedLoginResponse(result.get(1).toString());
            }
        } catch (RemoteException e) {
            logger.info("FAILED: ", e);
            return LoginResponse.getFailedLoginResponse(e.toString());
        }
    }
}
