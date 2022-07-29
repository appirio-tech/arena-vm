package com.topcoder.server.ejb.ProblemServices;

import javax.ejb.*;
import java.rmi.RemoteException;

public interface ProblemServicesHome extends EJBHome {

    public ProblemServices create() throws CreateException, RemoteException;
}
