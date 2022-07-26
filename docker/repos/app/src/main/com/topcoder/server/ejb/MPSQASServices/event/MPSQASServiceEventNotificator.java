/*
 * MPSQASServiceEventNotificator
 * 
 * Created 04/25/2006
 */
package com.topcoder.server.ejb.MPSQASServices.event;

import com.topcoder.shared.util.logging.Logger;
import com.topcoder.shared.serviceevent.ServiceEventNotificator;
import com.topcoder.shared.util.DBMS;

/**
 * Notificator class used by the MPSQASServices to notify events.
 *  
 * @author Diego Belfer (Mural)
 * @version $Id: MPSQASServiceEventNotificator.java 70823 2008-05-27 20:49:33Z dbelfer $
 */
public class MPSQASServiceEventNotificator {
    public static final String SERVICE_NAME = "MPSQASService";
    public static final String AVAILABLE_TEST_RESULTS = "availableTestResults";

    private Logger logger = Logger.getLogger(MPSQASServiceEventNotificator.class);

    /**
     * The real notificator 
     */
    private ServiceEventNotificator notificator =  null;

    
    /**
     * Constructs a new MPSQASServiceEventNotificator.
     * 
     * @throws IllegalStateException If a problem arises when triying to start the
     *                               notification mechanism
     */
    public MPSQASServiceEventNotificator() throws IllegalStateException {
        logger.info("Initializing MPSQASServiceNotificator...");
        notificator = new ServiceEventNotificator(DBMS.MPSQAS_SVC_EVENT_TOPIC, SERVICE_NAME);
        logger.info("Initialized MPSQASServiceEventNotificator");
    }
    
    /**
     * Notify all listeners about the new available test results
     * 
     * @param testResults test results that are available
     */
    public void notifyAvailableTestResults(MPSQASTestResult testResults) {
        logger.debug("Notifiying available results for to user="+testResults.getUserId());
        notificator.notifyEvent(AVAILABLE_TEST_RESULTS, testResults);
    }
}
