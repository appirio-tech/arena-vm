package com.topcoder.server.ejb.TrackingServices;

import javax.ejb.*;
import java.rmi.RemoteException;

public interface TrackingServicesHome extends EJBHome {

    public TrackingServices create() throws CreateException, RemoteException;
}
