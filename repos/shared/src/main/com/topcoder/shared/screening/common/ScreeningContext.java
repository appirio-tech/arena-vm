package com.topcoder.shared.screening.common;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Created by IntelliJ IDEA.
 * User: gtsipol
 * Date: Dec 19, 2002
 * Time: 6:17:17 PM
 * To change this template use Options | File Templates.
 */
public final class ScreeningContext {

    private ScreeningContext() {
    }

    public static Context getEJBContext() throws NamingException {
        return getContext(ScreeningApplicationServer.EJB_JNDI_FACTORY, ScreeningApplicationServer.EJB_SERVER_URL);
    }

    public static Context getJMSContext() throws NamingException {
//        return getContext(ScreeningApplicationServer.JMS_JNDI_FACTORY, ScreeningApplicationServer.JMS_SERVER_URL);
        return getContext(ScreeningApplicationServer.JMS_JNDI_FACTORY, ScreeningApplicationServer.JMS_SERVER_URL);
    }

    public static Context getContext(String initialContextFactory, String providerUrl) throws NamingException {
        Hashtable env = new Hashtable();
        env.put(Context.INITIAL_CONTEXT_FACTORY, initialContextFactory);
        env.put(Context.PROVIDER_URL, providerUrl);
        return new InitialContext(env);
    }

}
