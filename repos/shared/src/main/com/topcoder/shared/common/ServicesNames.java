/*
 * Copyright (C) 2006 - 2013 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.shared.common;

/**
 * Services names container.
 * 
 * Ad-hoc solution to avoid usage of ApplicationServer when using services
 * from the web app.
 *
 * <p>
 *     Version 1.1 (TC Competition Engine - Switch to use LDAP for authentication) change note:
 *     <ol>
 *         <li>Updated {@link #LOGIN_SERVICES} field.</li>
 *     </ol>
 * </p>
 * @author Diego Belfer (mural), TCSASSEMBLER
 * @version 1.1
 */
public interface ServicesNames {
    public final static String EMAIL_QUEUE = "eMailQueue";
    public final static String TEST_SERVICES = "jma.TestServicesHome";
    public final static String LONG_CONTEST_SERVICES = "jma.TestServicesHome";
    public final static String DB_SERVICES = "jma.DBServicesHome";
    public final static String TRACKING_SERVICES = "jma.TrackingServicesHome";
    public final static String MPSQAS_SERVICES = "jma.MPSQASServicesHome";
    public final static String PROBLEM_SERVICES = "jma.ProblemServicesHome";
    /**
     * The JNDI name of security login service.
     */
    public final static String LOGIN_SERVICES = "com.topcoder.security.login.LoginRemoteHome";
    public final static String PACTS_CLIENT_SERVICES = "com.topcoder.web.ejb.pacts.PactsClientServicesHome";
    public static final String ADMIN_SERVICES = "com.topcoder.server.ejb.AdminServicesHome";
}
