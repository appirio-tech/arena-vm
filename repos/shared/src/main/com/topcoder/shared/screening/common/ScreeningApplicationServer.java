package com.topcoder.shared.screening.common;

import com.topcoder.shared.util.SimpleResourceBundle;

/**
 * Created by IntelliJ IDEA.
 * User: gtsipol
 * Date: Dec 19, 2002
 * Time: 6:19:04 PM
 * To change this template use Options | File Templates.
 */
public final class ScreeningApplicationServer {

    private ScreeningApplicationServer() {
    }

    private static final SimpleResourceBundle bundle = SimpleResourceBundle.getBundle("ScreeningApplicationServer");
    //public static String SERVER_NAME = "localhost";
    //private static String DEFAULT_CONTEST_HOST_URL = "t3://172.16.20.40:9003";
    final static String EJB_SERVER_URL = getProperty("EJB_SERVER_URL");
    final static String JMS_SERVER_URL = getProperty("JMS_SERVER_URL");
    //private final static String DEFAULT_JNDI_FACTORY = "weblogic.jndi.WLInitialContextFactory";
    final static String EJB_JNDI_FACTORY = getProperty("EJB_JNDI_FACTORY");
    final static String JMS_JNDI_FACTORY = getProperty("JMS_JNDI_FACTORY");
//    public final static String JMS_FACTORY = getProperty("JMS_FACTORY");
    public final static String SCREENING_SERVICES = "com.topcoder.server.screening.ejb.ScreeningServicesHome";
    public final static String SCREENING_PROBLEM_SERVICES = "jma.ProblemServicesHome";
    public final static String TESTER_COMPILER_SERVICES = "com.topcoder.server.screening.ejb.ScreeningTesterCompilerServicesHome";
    public final static String ADMIN_SERVICES = "com.topcoder.server.screening.ejb.ScreeningAdminServicesHome";

    public final static long WEB_SERVER_ID = 1;

    private static String getProperty(String key) {
        return bundle.getString(key);
    }

}
