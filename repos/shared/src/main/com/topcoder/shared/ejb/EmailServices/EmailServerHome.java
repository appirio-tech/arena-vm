package com.topcoder.shared.ejb.EmailServices;

import javax.ejb.CreateException;
import javax.ejb.EJBHome;
import java.rmi.RemoteException;

/**
 *
 * @author   Eric Ellingson
 * @version  $Revision$
 */
public interface EmailServerHome extends EJBHome {
    /**
     *
     * @return
     * @throws CreateException
     * @throws RemoteException
     */
    public EmailServer create() throws CreateException, RemoteException;
}

