/*
 * DBServicesLocator
 *
 * Created 11/30/2006
 */
package com.topcoder.server.ejb.DBServices;

import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.naming.NamingException;

import com.topcoder.shared.common.ServicesNames;
import com.topcoder.shared.ejb.ServiceLocatorSupport;
import com.topcoder.shared.util.ApplicationServer;



/**
 * Helper class to obtain the DBServices
 *
 * @author Diego Belfer (mural)
 * @version $Id: DBServicesLocator.java 59940 2007-04-17 16:20:14Z thefaxman $
 */
public class DBServicesLocator {
    private static final ServiceLocatorSupport locator =
        new ServiceLocatorSupport(DBServices.class, DBServicesHome.class,
                ServicesNames.DB_SERVICES, ApplicationServer.CONTEST_HOST_URL);

    public static DBServices getService() throws NamingException, RemoteException, CreateException {
        return (DBServices) locator.getService();
    }
}
