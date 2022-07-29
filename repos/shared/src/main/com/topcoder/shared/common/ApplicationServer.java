package com.topcoder.shared.common;

//import java.util.MissingResourceException;
//import java.util.ResourceBundle;

import com.topcoder.shared.util.SimpleResourceBundle;

//import com.topcoder.server.ejb.ProblemServices.*;

//import javax.naming.*;

public final class ApplicationServer implements ServicesNames {

    private ApplicationServer() {
    }

    private static final SimpleResourceBundle bundle = SimpleResourceBundle.getVariationBundle("ApplicationServer");

    //Environments
    //static final int PROD     = 1;
    //static final int QA       = 2;
    //static final int DEV      = 3;


    //static int                   ENVIRONMENT                  = DEV;
    public static String SERVER_NAME = "172.16.20.20";
    //private static String        DEFAULT_CONTEST_HOST_URL     = "t3://172.16.20.40:9003";
    final static String EJB_SERVER_URL = getProperty("EJB_SERVER_URL");
    final static String JBOSS_EJB_SERVER_URL = getProperty("JBOSS_EJB_SERVER_URL");
    final static String JMS_SERVER_URL = getProperty("JMS_SERVER_URL");
    public static String[] WEBLOGIC_CLUSTER_IP = {"172.16.20.20"};
    //public static String         BASE_DIR                     = "/app/build/classes/com/topcoder/server/";


//  public final static String RESOURCES = "/app/resources";

    // note - this is a resource path, not a UNIX file path
    // usually goes in /app/resources
    public final static String IAGREE = "/terms.txt";
    //private final static String DEFAULT_JNDI_FACTORY="weblogic.jndi.WLInitialContextFactory";
    final static String EJB_JNDI_FACTORY = getProperty("EJB_JNDI_FACTORY");
    final static String JMS_JNDI_FACTORY = getProperty("JMS_JNDI_FACTORY");
    final static String JBOSS_EJB_JNDI_FACTORY = getProperty("JBOSS_EJB_JNDI_FACTORY");
    public final static String JMS_FACTORY = getProperty("JMS_FACTORY");

    /**
     * The String representing JNDI lookup name for Admin Services EJB. The
     * initial value is set with value of "ADMIN_SERVICES_JNDI" property from
     * resource bundle.
     *
     * @since Admin Tool 2.0
     */
    public final static String ADMIN_SERVICES = getProperty("ADMIN_SERVICES_JNDI");

    private static String getProperty(String key) {
        return bundle.getString(key);
    }

    
}
