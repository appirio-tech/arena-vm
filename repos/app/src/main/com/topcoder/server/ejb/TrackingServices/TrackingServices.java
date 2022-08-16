package com.topcoder.server.ejb.TrackingServices;

import javax.ejb.EJBObject;
import java.rmi.RemoteException;
//import java.util.ArrayList;
//import java.util.HashMap;
import com.topcoder.server.common.Tracking;

//import com.topcoder.server.common.attr.*;
//import com.topcoder.server.tester.*;

public interface TrackingServices extends EJBObject {

    public void storeTracking(Tracking t) throws RemoteException;
//  public ArrayList retrieveSince(long time) throws RemoteException;
}
