package com.topcoder.server.ejb.DBServices;

import javax.ejb.*;
import java.rmi.RemoteException;

/**
 *
 * This class creates an instance of WorldServices ejb.
 *
 */

public interface DBServicesHome extends EJBHome {

    /**
     *
     * This method returns a DBServices ejb.
     *
     */

    DBServices create() throws CreateException, RemoteException;
}
