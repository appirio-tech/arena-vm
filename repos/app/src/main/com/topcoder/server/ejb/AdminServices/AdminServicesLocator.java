/*
 * AdminServicesLocator
 *
 * Created 04/23/2008
 */
package com.topcoder.server.ejb.AdminServices;

import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.naming.NamingException;

import com.topcoder.shared.common.ServicesNames;
import com.topcoder.shared.ejb.ServiceLocatorSupport;
import com.topcoder.shared.util.ApplicationServer;



/**
 * Helper class to obtain the AdminServices
 *
 * @author Diego Belfer (mural)
 * @version $id$
 */
public class AdminServicesLocator {
    private static final ServiceLocatorSupport locator =
        new ServiceLocatorSupport(AdminServices.class, AdminServicesHome.class,
                ServicesNames.ADMIN_SERVICES , ApplicationServer.CONTEST_HOST_URL);

    public static AdminServices getService() throws NamingException, RemoteException, CreateException {
        return (AdminServices) locator.getService();
    }
}
