/*
 * MPSQASLongTestServiceListener
 * 
 * Created 04/25/2006
 */
package com.topcoder.server.ejb.MPSQASServices;

import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.naming.NamingException;

import com.topcoder.server.ejb.TestServices.longtest.event.LongTestServiceEventListener;
import com.topcoder.server.ejb.TestServices.longtest.event.TestGroupFinalizedProcessor;
import com.topcoder.shared.util.logging.Logger;

/**
 * @author Diego Belfer (mural)
 * @version $Id: MPSQASLongTestServiceListener.java 70823 2008-05-27 20:49:33Z dbelfer $
 */
class MPSQASLongTestServiceListener implements TestGroupFinalizedProcessor {
    private final Logger log = Logger.getLogger(MPSQASLongTestServiceListener.class);
    private MPSQASServices mpsqasServices;
    private LongTestServiceEventListener listener = null;

    MPSQASLongTestServiceListener() {
        listener = new LongTestServiceEventListener();
        listener.addTestGroupFinalizedProcessor(this);
    }
   
    /** Returns an instance of MPSQASServices. */
    private MPSQASServices getMPSQASServices() throws NamingException, CreateException, RemoteException {
        if (mpsqasServices == null) {
            mpsqasServices = MPSQASServicesLocator.getService();
        }
        return mpsqasServices;
    }
    
    public void testGroupFinalized(int testGroupId) {
        try {
            getMPSQASServices().testGroupFinalized(testGroupId);
        } catch (Exception e) {
            log.error("Cannot notify MPSQAS services about end of test group. TestGroupId=" + testGroupId);
        }   
    }
}
