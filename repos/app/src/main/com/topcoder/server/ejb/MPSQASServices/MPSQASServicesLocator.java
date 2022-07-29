/*
 * MPSQASServicesLocator
 *
 * Created 04/23/2008
 */
package com.topcoder.server.ejb.MPSQASServices;

import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.naming.NamingException;

import com.topcoder.shared.common.ServicesNames;
import com.topcoder.shared.ejb.ServiceLocatorSupport;
import com.topcoder.shared.util.ApplicationServer;



/**
 * Helper class to obtain the MPSQASServices
 *
 * @author Diego Belfer (mural)
 * @version $id$
 */
public class MPSQASServicesLocator {
    private static final ServiceLocatorSupport locator =
        new ServiceLocatorSupport(MPSQASServices.class, MPSQASServicesHome.class,
                ServicesNames.MPSQAS_SERVICES , ApplicationServer.CONTEST_HOST_URL);

    public static MPSQASServices getService() throws NamingException, RemoteException, CreateException {
        return (MPSQASServices) locator.getService();
    }
}
