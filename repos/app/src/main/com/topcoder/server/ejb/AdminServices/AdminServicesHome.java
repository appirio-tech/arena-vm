package com.topcoder.server.ejb.AdminServices;

import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.ejb.EJBHome;

/**
 * The home interface for the Admin Services EJB.
 *
 * @author  Dave Pecora
 * @version 1.00, 06/01/2002
 */

public interface AdminServicesHome extends EJBHome {

    /**
     * Creates and returns an <tt>AdminServices</tt> object.
     *
     * @return  A new <tt>AdminServices</tt> object.
     */
    AdminServices create() throws CreateException, RemoteException;
}

