package com.topcoder.server.ejb.TestServices;

import javax.ejb.*;
import java.rmi.RemoteException;

public interface TestServicesHome extends EJBHome {

    public TestServices create() throws CreateException, RemoteException;
}
