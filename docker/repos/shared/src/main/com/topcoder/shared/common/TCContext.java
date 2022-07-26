package com.topcoder.shared.common;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;


public final class TCContext {
    private TCContext() {
    }

    public static Context getEJBContext() throws NamingException {
        return getContext(ApplicationServer.EJB_JNDI_FACTORY, ApplicationServer.EJB_SERVER_URL);
    }

    public static Context getJbossContext() throws NamingException {
        return getContext(ApplicationServer.JBOSS_EJB_JNDI_FACTORY, ApplicationServer.JBOSS_EJB_SERVER_URL);
    }

    public static Context getJMSContext() throws NamingException {
        return getContext(ApplicationServer.JMS_JNDI_FACTORY, ApplicationServer.JMS_SERVER_URL);
    }

    public static Context getContext(String initialContextFactory, String providerUrl) throws NamingException {
        Hashtable env = new Hashtable();
        env.put(Context.INITIAL_CONTEXT_FACTORY, initialContextFactory);
        env.put(Context.PROVIDER_URL, providerUrl);
        env.put("jnp.disableDiscovery","true");//stop jboss from multicasting
        return new InitialContext(env);
    }

}
