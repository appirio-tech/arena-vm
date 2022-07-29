/*
 * LongContestServicesLocator
 *
 * Created 05/09/2006
 */
package com.topcoder.server.ejb.TestServices;

import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.naming.NamingException;

import com.topcoder.shared.common.ServicesNames;
import com.topcoder.shared.ejb.ServiceLocatorSupport;
import com.topcoder.shared.util.ApplicationServer;



/**
 * Helper class to obtain the TestServices
 *
 * @author Diego Belfer (mural)
 * @version $Id: TestServicesLocator.java 65513 2007-09-24 19:31:43Z thefaxman $
 */
public class TestServicesLocator {
    private static ServiceLocatorSupport locator =
        new ServiceLocatorSupport(TestServices.class, TestServicesHome.class,
                ServicesNames.TEST_SERVICES, ApplicationServer.CONTEST_HOST_URL);

    public static TestServices getService() throws NamingException, RemoteException, CreateException {
        return (TestServices) locator.getService();
    }

}