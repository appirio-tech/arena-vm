/*
 * MPSQASServiceEventListener
 * 
 * Created 04/25/2006
 */
package com.topcoder.server.ejb.MPSQASServices.event;

import java.io.Serializable;

import com.topcoder.shared.serviceevent.ServiceEventHandler;
import com.topcoder.shared.serviceevent.ServiceEventListener;
import com.topcoder.shared.serviceevent.ServiceEventMessageListener;
import com.topcoder.shared.util.DBMS;

/**
 * Listener process to receive events from the MPSQASServices
 * 
 * @author Diego Belfer (mural)
 * @version $Id: MPSQASServiceEventListener.java 74113 2008-12-29 19:34:43Z dbelfer $
 */
public class MPSQASServiceEventListener  {
    /**
     * The real listener object
     */
    private ServiceEventListener serviceListener;
    private ServiceEventMessageListener messageListener;

    /**
     * Creates a new MPSQASServiceEventListener
     * 
     * @throws IllegalStateException If a problem arises when trying to start the
     *                              listening mechanism
     */
    public MPSQASServiceEventListener() throws IllegalStateException {
        messageListener = new ServiceEventMessageListener(MPSQASServiceEventNotificator.SERVICE_NAME);
        serviceListener = new ServiceEventListener(DBMS.MPSQAS_SVC_EVENT_TOPIC, messageListener);
    }

    /**
     * Adds the processor to process incoming events about 
     * AvailableTestResults.
     * 
     * @param processor The processor 
     */
    public void addAvailableTestResultsProcessor(final MPSQASServiceEventProcessor processor) {
        messageListener.addListener(MPSQASServiceEventNotificator.AVAILABLE_TEST_RESULTS, 
                new ServiceEventHandler() {
                    public void eventReceived(String eventType, Serializable object) {
                        processor.availableTestResults((MPSQASTestResult) object);
                    }
                });
    }

    /**
     * MPSQASServiceEventProcessor defines the required
     * methods that must implements processors added to 
     * this listener  
     */
    public interface MPSQASServiceEventProcessor {
        
        /**
         * Calling when a AvailableTestResults event is received
         * from the MPSQASServices
         * 
         * @param result The test result that is available
         */
        public void availableTestResults(MPSQASTestResult result);
    }
}
