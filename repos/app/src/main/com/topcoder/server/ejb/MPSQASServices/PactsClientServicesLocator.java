/*
 * PactsClientServicesLocator
 *
 * Created 05/09/2006
 */
package com.topcoder.server.ejb.MPSQASServices;

import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.naming.NamingException;

import com.topcoder.shared.common.ServicesNames;
import com.topcoder.shared.ejb.ServiceLocatorSupport;
import com.topcoder.shared.util.ApplicationServer;
import com.topcoder.web.ejb.pacts.PactsClientServices;
import com.topcoder.web.ejb.pacts.PactsClientServicesHome;




/**
 * Helper class to obtain the PACTs EJB, used by MPSQASServices
 *
 * @author rfairfax
 * @version $Id: PactsClientServicesLocator.java 65513 2007-09-24 19:31:43Z thefaxman $
 */
public class PactsClientServicesLocator {
    private static final ServiceLocatorSupport locator =
        new ServiceLocatorSupport(PactsClientServices.class, PactsClientServicesHome.class,
                ServicesNames.PACTS_CLIENT_SERVICES, ApplicationServer.PACTS_HOST_URL);

    public static PactsClientServices getService() throws NamingException, RemoteException, CreateException {
        return (PactsClientServices) locator.getService();
    }
}
