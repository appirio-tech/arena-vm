/*
 * Copyright (C) 2001 - 2009 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.shared.util;

import com.topcoder.shared.util.logging.Logger;

import javax.naming.Context;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * <p>This class holds application wide constants.</p>
 *
 * <p>
 *   Version 1.1 (Configurable Contest Terms Release Assembly v1.0) Change notes:
 *   <ol>
 *     <li>Added constants to configure Project Role User Terms Of Use Service host URL for service location.</li>
 *   </ol>
 * </p>
 *
 * <p>
 *   Version 1.2 (Competition Registration Eligibility v1.0) Change notes:
 *   <ol>
 *     <li>Added constant for Cockpit Service provider URL for service location.</li>
 *     <li>Added constant for Contest eligibility services JNDI name.</li>
 *   </ol>
 * </p>
 *
 * <p>
 *   Version 1.3 (Project View) Change notes:
 *   <ol>
 *     <li>Added constants for Pipeline Facade Services lookup.</li>
 *   </ol>
 * </p>
 *
 * <p>
 *   Version 1.4 (TCCC-5802  https://apps.topcoder.com/bugs/browse/TCCC-5802) Change notes:
 *   <ol>
 *     <li>Add SSO_COOKIE_NAME and SSO_HASH_SECRET.</li>
 *     <li>Add Auth0 related properties</li>
 *   </ol>
 * </p>
 *
 * <p>
 *   Version 1.5 (https://apps.topcoder.com/bugs/browse/BUGR-10718) Change notes:
 *   <ol>
 *     <li>Add JWT_COOKIE_KEY.</li>
 *   </ol>
 * </p>
 *
 * @author Steve Burrows, pulky, MonicaMuranyi
 * @version 1.5
 */
public class ApplicationServer {
    private static Logger log = Logger.getLogger(ApplicationServer.class);

    private static TCResourceBundle bundle = new TCResourceBundle("ApplicationServer");

    public static final int PROD = bundle.getIntProperty("PROD", 1);
    public static final int QA = bundle.getIntProperty("QA", 2);
    public static final int DEV = bundle.getIntProperty("DEV", 3);

    public static int ENVIRONMENT = bundle.getIntProperty("ENVIRONMENT", DEV);
    public static String SERVER_NAME = bundle.getProperty("SERVER_NAME", "172.16.20.20");
    public static String STUDIO_SERVER_NAME = bundle.getProperty("STUDIO_SERVER_NAME", "studio.dev.topcoder.com");
    public static String OPENAIM_SERVER_NAME = bundle.getProperty("OPENAIM_SERVER_NAME", "openaim.dev.topcoder.com");
    public static String TRUVEO_SERVER_NAME = bundle.getProperty("TRUVEO_SERVER_NAME", "www.dev.topcoder.com/truveo");
    public static String AOLICQ_SERVER_NAME = bundle.getProperty("AOLICQ_SERVER_NAME", "www.dev.topcoder.com/aolicq");
    public static String WINFORMULA_SERVER_NAME = bundle.getProperty("WINFORMULA_SERVER_NAME", "www.dev.topcoder.com/winformula");
    public static String CSF_SERVER_NAME = bundle.getProperty("CSF_SERVER_NAME", "csf.dev.topcoder.com");
    public static String CORP_SERVER_NAME = bundle.getProperty("CORP_SERVER_NAME", "172.16.20.20/corp");
    public static String SOFTWARE_SERVER_NAME = bundle.getProperty("SOFTWARE_SERVER_NAME", "172.16.20.222");
    public static String FORUMS_SERVER_NAME = bundle.getProperty("FORUMS_SERVER_NAME", "forums.topcoder.com");
	public static String COMMUNITY_SERVER_NAME = bundle.getProperty("COMMUNITY_SERVER_NAME", "community.topcoder.com");
    public static String HOST_URL = bundle.getProperty("HOST_URL", "t3://172.16.20.41:7030");
    public static String FORUMS_HOST_URL = bundle.getProperty("FORUMS_HOST_URL", "63.118.154.182:1099");
    public static String JMS_HOST_URL = bundle.getProperty("JMS_HOST_URL", "jnp://172.16.210.55:1100,jnp://172.16.210.56:1100");
    public static String CONTEST_HOST_URL = bundle.getProperty("CONTEST_HOST_URL", "t3://172.16.20.40:9003");
    public static String PACTS_HOST_URL = bundle.getProperty("PACTS_HOST_URL", "t3://172.16.20.40:9003");
    public static String USER_SERVICES_HOST_URL = bundle.getProperty("USER_SERVICES_HOST_URL", "t3://172.16.20.40:9003");
    public static String BASE_DIR = bundle.getProperty("BASE_DIR", "/usr/web/build/classes");
    public static String SECURITY_PROVIDER_URL = bundle.getProperty("SECURITY_PROVIDER_URL", "172.16.20.40:1099");
    public static String FILE_CONVERSION_PROVIDER_URL = bundle.getProperty("FILE_CONVERSION_PROVIDER_URL", "172.16.210.53:1099");
    public static String DISTRIBUTED_UI_SERVER_NAME = bundle.getProperty("DISTRIBUTED_UI_SERVER_NAME", "63.118.154.181:9380");
    public static String OR_WEBSERVICES_SERVER_NAME = bundle.getProperty("OR_WEBSERVICES_SERVER_NAME", "63.118.154.186:8080");
    public static String WIKI_SERVER_NAME = bundle.getProperty("WIKI_SERVER_NAME", "www.dev.topcoder.com/wiki");
    public static String NEW_COMMUNITY_SERVER_NAME = bundle.getProperty("NEW_COMMUNITY_SERVER_NAME", "www.topcoder.com");

    public static String SSO_DOMAIN = bundle.getProperty("SSO_DOMAIN", ".topcoder.com");

    public final static String JNDI_FACTORY = bundle.getProperty("JNDI_FACTORY", "weblogic.jndi.WLInitialContextFactory");
    public final static String JMS_FACTORY = bundle.getProperty("JMS_FACTORY", "jms.connection.jmsFactory");
    public final static String JMS_BKP_FACTORY = bundle.getProperty("JMS_BKP_FACTORY", "jms.connection.jmsFactory_BKP");
    public final static String TRANS_FACTORY = bundle.getProperty("TRANS_FACTORY", "javax.transaction.UserTransaction");
    public final static String TRANS_MANAGER = bundle.getProperty("TRANS_MANAGER", "weblogic/transaction/TransactionManager");
    public final static String SECURITY_CONTEXT_FACTORY = bundle.getProperty("SECURITY_CONTEXT_FACTORY", "org.jnp.interfaces.NamingContextFactory");

    public final static String STUDIO_SERVICES_PROVIDER_URL = bundle.getProperty("STUDIO_SERVICES_PROVIDER_URL", "127.0.0.1:1399");
    public final static String STUDIO_SERVICES_USERNAME = bundle.getProperty("STUDIO_SERVICES_USERNAME", "user");
    public final static String STUDIO_SERVICES_PASSWORD = bundle.getProperty("STUDIO_SERVICES_PASSWORD", "password");
    public final static String STUDIO_SERVICES_CONTEXT_FACTORY = bundle.getProperty("STUDIO_SERVICES_CONTEXT_FACTORY", "org.jboss.security.jndi.LoginInitialContextFactory");
    public final static String STUDIO_SERVICES_PKG_PREFIXES = bundle.getProperty("STUDIO_SERVICES_PKG_PREFIXES", "org.jboss.naming:org.jnp.interfaces");
    public final static String STUDIO_SERVICES_PROTOCOL = bundle.getProperty("STUDIO_SERVICES_PROTOCOL", "cockpitDomain");
    public final static String STUDIO_SERVICES_JNDI_NAME = bundle.getProperty("STUDIO_SERVICES_JNDI_NAME", "StudioServiceBean/remote");

    public final static int SESSION_ID_LENGTH = bundle.getIntProperty("SESSION_ID_LENGTH", 50);

    public static String TCS_APP_SERVER_URL = bundle.getProperty("TCS_APP_SERVER_URL", "172.16.20.222:1099");
    public final static int WEB_SERVER_ID = bundle.getIntProperty("WEB_SERVER_ID", 1);

    /**
     * Host URL for terms of use related services
     *
     * @since 1.1
     */
    public static String TERMS_OF_USE_HOST_URL = bundle.getProperty("TERMS_OF_USE_HOST_URL", "jnp://localhost:1199");

    /**
     * Provider URL for cockpit related services
     *
     * @since 1.2
     */
    public static String COCKPIT_PROVIDER_URL = bundle.getProperty("COCKPIT_PROVIDER_URL", "jnp://localhost:1199");

    /**
     * The contest eligibility services JNDI name
     *
     * @since 1.2
     */
    public static String CONTEST_ELIGIBILITY_SERVICES_JNDI_NAME =
        bundle.getProperty("CONTEST_ELIGIBILITY_SERVICES_JNDI_NAME", "remote/ContestEligibilityServiceBean");

    /**
     * The pipeline service facade JNDI name
     *
     * @since 1.3
     */
    public static String PIPELINE_SERVICE_FACADE_JNDI_NAME =
        bundle.getProperty("PIPELINE_SERVICE_FACADE_JNDI_NAME", "remote/PipelineServiceFacadeBean");

    /**
     * The pipeline service facade username
     *
     * @since 1.3
     */
    public final static String PIPELINE_SERVICE_FACADE_USERNAME =
        bundle.getProperty("PIPELINE_SERVICE_FACADE_USERNAME", "user");

    /**
     * The pipeline service facade password
     *
     * @since 1.3
     */
    public final static String PIPELINE_SERVICE_FACADE_PASSWORD =
        bundle.getProperty("PIPELINE_SERVICE_FACADE_PASSWORD", "password");

    /**
     * The pipeline service facade context factory
     *
     * @since 1.3
     */
    public final static String PIPELINE_SERVICE_FACADE_CONTEXT_FACTORY =
        bundle.getProperty("PIPELINE_SERVICE_FACADE_CONTEXT_FACTORY", "org.jboss.security.jndi.LoginInitialContextFactory");

    /**
     * The pipeline service facade pkg prefixes
     *
     * @since 1.3
     */
    public final static String PIPELINE_SERVICE_FACADE_PKG_PREFIXES =
        bundle.getProperty("PIPELINE_SERVICE_FACADE_PKG_PREFIXES", "org.jboss.naming:org.jnp.interfaces");

    /**
     * The pipeline service facade protocol
     *
     * @since 1.3
     */
    public final static String PIPELINE_SERVICE_FACADE_PROTOCOL =
        bundle.getProperty("PIPELINE_SERVICE_FACADE_PROTOCOL", "other");

    /**
     * <p>The name for SSO cookie.</p>
     * @since 1.4
     */
    public final static String SSO_COOKIE_KEY =
        bundle.getProperty("SSO_COOKIE_KEY", "tcsso");

    /**
     * <p>
     * Just some random junk no one else knows, for SSO cookie hash purpose.
     * </p>
     * @since 1.4
     */
    public final static String SSO_HASH_SECRET =
        bundle.getProperty("SSO_HASH_SECRET", "");


    /**
     * <p>A <code>String</code> providing the client id in auth0.com to enable login with 
     * social accounts like Google, Facebook.</p>
     *
     * @since 1.4
     */
    public final static String CLIENT_ID_AUTH0 =
        bundle.getProperty("CLIENT_ID_AUTH0", "CMaBuwSnY0Vu68PLrWatvvu3iIiGPh7t");
    

    /**
     * <p>The callback url of the Auth0 account.</p>
     * 
     * @since 1.4
     */
    public final static String REDIRECT_URL_AUTH0 = 
        bundle.getProperty("REDIRECT_URL_AUTH0", "/reg2/callback.action");

    /**
     * <p>The server name of tc_reg_revamp host.</p>
     * 
     *  @since 1.4
     */
    public final static String REG_SERVER_NAME =
        bundle.getProperty("REG_SERVER_NAME", "tc.cloud.topcoder.com");

    /**
     * <p>The auth0 domain.</p>
     * 
     *  @since 1.4
     */
    public final static String DOMAIN_AUTH0 =
        bundle.getProperty("DOMAIN_AUTH0", "topcoder.auth0.com");

    /**
     * <p>
     * The json web token
     * </p>
     * @since 1.5
     */
    public final static String JWT_COOKIE_KEY =
        bundle.getProperty("JWT_COOKIE_KEY", "");

    public static void close(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (Exception ignore) {
                log.error("FAILED to close ResultSet.");
                ignore.printStackTrace();
            }
        }
    }

    public static void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (Exception ignore) {
                log.error("FAILED to close Connection.");
                ignore.printStackTrace();
            }
        }

    }

    public static void close(Context ctx) {
        if (ctx != null) {
            try {
                ctx.close();
            } catch (Exception ignore) {
                log.error("FAILED to close Context.");
                ignore.printStackTrace();
            }
        }

    }

    public static void close(PreparedStatement ps) {
        if (ps != null) {
            try {
                ps.close();
            } catch (Exception ignore) {
                log.error("FAILED to close PreparedStatement.");
                ignore.printStackTrace();
            }
        }

    }


}
