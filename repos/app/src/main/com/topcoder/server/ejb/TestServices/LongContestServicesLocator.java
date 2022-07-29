/*
 * LongContestServicesLocator
 *
 * Created 07/17/2007
 */
package com.topcoder.server.ejb.TestServices;

import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.naming.NamingException;

import com.topcoder.server.ejb.asyncservices.AsyncServiceClientInvoker;
import com.topcoder.server.ejb.asyncservices.AsyncServiceProxyGenerator;
import com.topcoder.shared.common.ServicesNames;
import com.topcoder.shared.ejb.ServiceLocatorSupport;
import com.topcoder.shared.util.ApplicationServer;



/**
 * Helper class to obtain the LongContestServices
 *
 * @author Diego Belfer (mural)
 * @version $Id: LongContestServicesLocator.java 65513 2007-09-24 19:31:43Z thefaxman $
 */
public class LongContestServicesLocator {
    private static final String hostURL = ApplicationServer.CONTEST_HOST_URL;
    private static final String serviceName = ServicesNames.LONG_CONTEST_SERVICES;
    private static final Class homeInterfaceClass = TestServicesHome.class;
    private static final Class serviceInterfaceClass = LongContestServices.class;

    private static ServiceLocatorSupport locator = new ServiceLocatorSupport(serviceInterfaceClass, homeInterfaceClass, serviceName, hostURL);

    public static LongContestServices getService() throws NamingException, RemoteException, CreateException {
        return (LongContestServices) locator.getService();
    }
    
    public static LongContestServices getAsyncService(AsyncServiceClientInvoker.AsyncResponseHandler handler, long timeToLive, Object responseId) throws NamingException {
        return (LongContestServices) AsyncServiceProxyGenerator.getAsyncService(hostURL, serviceName, homeInterfaceClass, serviceInterfaceClass, handler, timeToLive, responseId);
    }
}