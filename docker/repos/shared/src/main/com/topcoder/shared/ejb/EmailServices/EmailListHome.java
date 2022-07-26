package com.topcoder.shared.ejb.EmailServices;

import javax.ejb.CreateException;
import javax.ejb.EJBHome;
import java.rmi.RemoteException;

/**
 * @author   Eric Ellingson
 * @version  $Revision$
 */
public interface EmailListHome extends EJBHome {
    /**
     *
     * @return
     * @throws CreateException
     * @throws RemoteException
     */
    public EmailList create() throws CreateException, RemoteException;
}

