package com.topcoder.server.services.authenticate;

import com.topcoder.server.common.User;
import com.topcoder.shared.common.ApplicationServer;
import com.topcoder.shared.common.TCContext;
import com.topcoder.shared.util.DBMS;
import com.topcoder.shared.util.TCResourceBundle;
import com.topcoder.security.TCSubject;
import com.topcoder.security.login.AuthenticationException;
import com.topcoder.security.login.LoginRemote;
import com.topcoder.security.login.LoginRemoteHome;

import javax.naming.Context;

/**
 * Created by IntelliJ IDEA.
 * User: gtsipol
 * Date: Jan 10, 2003
 * Time: 11:55:03 AM
 * To change this template use Options | File Templates.
 */
public class CommonOLTPAuthenticatorClient {
    /**
     * the mock file name.
     */
    private static final String MOCK_BASE_CONF = "Authentication";    
    /**
     * the mock configuration.
     */
    private static final TCResourceBundle mockBundle = new TCResourceBundle(MOCK_BASE_CONF);
    /**
     * the security mock login configuration.
     */
    private static final String MOCK_LOGIN_KEY = "mock.login";
    /**
     * the default mock value is alway false.
     */
    private static final String DEFAULT_MOCK_VALUE = "false";
    /**
     * to check if it is needed to mock login.
     * @return
     *      the default is false to indicate not mock login
     *      true: we mock login
     */
    public static boolean isMockedLogin() {
        return Boolean.parseBoolean(mockBundle.getProperty(MOCK_LOGIN_KEY,DEFAULT_MOCK_VALUE));
    }
    public static TCSubject authenticate(String username, String password) throws AuthenticationException {
        TCSubject tcSubject = null;
        try {		
            // if not mocked login
            if(!isMockedLogin()) {
                Context ctx = TCContext.getJbossContext();
                LoginRemoteHome home = (LoginRemoteHome) ctx.lookup(ApplicationServer.LOGIN_SERVICES);
                LoginRemote loginService = home.create();

                // authenticate user
                // AuthenticationException thrown if can't authenticate
                tcSubject = loginService.login(username, password);
            } else {
                // it is just mocked here for pass authentication
                tcSubject = (new MockLoginProcessor()).processRequest(username, password);
            }
        } catch (AuthenticationException e) {
            //no need to print login failures
            //DBMS.printException(e);
            // just rethrow it
            throw e;
        } catch (Exception e) {
            DBMS.printException(e);
        }
        return tcSubject;
    }
}
