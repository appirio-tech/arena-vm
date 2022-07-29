/*
 * LongTestServiceEventNotificator
 * 
 * Created 04/25/2006
 */
package com.topcoder.server.ejb.TestServices.longtest.event;

import com.topcoder.shared.util.logging.Logger;
import com.topcoder.shared.serviceevent.ServiceEventNotificator;
import com.topcoder.shared.util.DBMS;

/**
 * Notificator class used by the LongTestServiceBean to notify events.
 *  
 * @author Diego Belfer (Mural)
 * @version $Id: LongTestServiceEventNotificator.java 70823 2008-05-27 20:49:33Z dbelfer $
 */
public class LongTestServiceEventNotificator {
    public static final String SERVICE_NAME = "LongTestService";
    public static final String EVENT_TEST_GROUP_FINISHED = "testGroupFinished";
    private Logger logger = Logger.getLogger(LongTestServiceEventNotificator.class);

    /**
     * The real notificator 
     */
    private ServiceEventNotificator notificator =  null;

    /**
     * Constructs a new LongTestServiceEventNotificator.
     * 
     * @throws IllegalStateException If a problem arises when triying to start the
     *                               notification mechanism
     */
    public LongTestServiceEventNotificator() throws IllegalStateException {
        logger.info("Initializing LongTestServiceNotificator...");
        notificator = new ServiceEventNotificator(DBMS.LONG_TEST_SVC_EVENT_TOPIC, SERVICE_NAME);
        logger.info("Initialized LongTestServiceEventNotificator");
    }

    /**
     * Notify all listeners about test group finalization
     * 
     * @param testGroupId Id of the test group finished
     */
    public void notifyTestGroupFinished(int testGroupId) {
        notificator.notifyEvent(EVENT_TEST_GROUP_FINISHED, new Integer(testGroupId));
    }
}
