/*
 * Copyright (C) 2006-2013 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.client.mpsqasApplet.messaging;

import java.util.Set;

import com.topcoder.netCommon.mpsqas.LookupValues;
import com.topcoder.netCommon.mpsqas.communication.message.LoginResponse;

/**
 * <p>
 * Processes login responses.
 * </p>
 *
 * <p>
 * <strong>Change log:</strong>
 * </p>
 *
 * <p>
 * Version 1.1 (Release Assembly - Dynamic Round Type List For Long and Individual Problems):
 * <ol>
 * <li>
 * Updated {@link #processAcceptedLogin(boolean, boolean, boolean, LookupValues)} to support
 * {@link com.topcoder.netCommon.mpsqas.LookupValues} parameter.
 * </li>
 * </ol>
 * </p>
 *
 * <p>
 * <strong>Thread Safety: </strong><br/>
 * This class is mutable and not thread-safe.
 * </p>
 *
 * @author TCSASSEMBLER
 * @version 1.1
 */
public interface LoginResponseProcessor {

    public void processRefusedLogin(String reason);

    /**
     * Processes accepted login.
     *
     * @param id the user's id
     * @param admin set to true if the user was authenticated as an administrator
     * @param writer whether the user has writer privileges
     * @param tester whether the user has tester privileges
     * @param lookupValues lookup values
     */
    public void processAcceptedLogin(boolean isAdmin, boolean isWriter,
            boolean isTester, LookupValues lookupValues);
}
