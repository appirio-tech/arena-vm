package com.topcoder.server.ejb.MPSQASServices;

//package ejb.MPSQASServices;

import javax.ejb.*;
import java.rmi.RemoteException;

/**
 * The home for the MPSQASServicesBean
 *
 * @author mitalub
 */
public interface MPSQASServicesHome extends EJBHome {

    public MPSQASServices create() throws CreateException, RemoteException;
}
