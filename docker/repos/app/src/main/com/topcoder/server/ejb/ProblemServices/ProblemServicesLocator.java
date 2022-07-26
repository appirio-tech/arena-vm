/*
 * ProblemServicesLocator
 *
 * Created 05/18/2006
 */
package com.topcoder.server.ejb.ProblemServices;

import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.naming.NamingException;

import com.topcoder.shared.common.ServicesNames;
import com.topcoder.shared.ejb.ServiceLocatorSupport;
import com.topcoder.shared.util.ApplicationServer;



/**
 * Helper class to obtain the ProblemServices
 *
 * @author Diego Belfer (mural)
 * @version $Id: ProblemServicesLocator.java 59940 2007-04-17 16:20:14Z thefaxman $
 */
public class ProblemServicesLocator {
    private static final ServiceLocatorSupport locator =
        new ServiceLocatorSupport(ProblemServices.class, ProblemServicesHome.class,
                ServicesNames.PROBLEM_SERVICES, ApplicationServer.CONTEST_HOST_URL);

    public static ProblemServices getService() throws NamingException, RemoteException, CreateException {
        return (ProblemServices) locator.getService();
    }
}
