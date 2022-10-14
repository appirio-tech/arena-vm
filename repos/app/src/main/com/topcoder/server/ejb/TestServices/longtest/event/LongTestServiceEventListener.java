/*
 * LongTestServiceEventListener
 * 
 * Created 04/25/2006
 */
package com.topcoder.server.ejb.TestServices.longtest.event;

import java.io.Serializable;

import com.topcoder.shared.serviceevent.ServiceEventHandler;
import com.topcoder.shared.serviceevent.ServiceEventListener;
import com.topcoder.shared.serviceevent.ServiceEventMessageListener;
import com.topcoder.shared.util.DBMS;

/**
 * Listener process to receive events from the LongTestServices
 *  
 * @author Diego Belfer (mural)
 * @version $Id: LongTestServiceEventListener.java 74113 2008-12-29 19:34:43Z dbelfer $
 */
public class LongTestServiceEventListener  {
    /**
     * The real listener
     */
    private ServiceEventListener serviceListener;

    /**
     * Creates a new LongTestServiceEventListener
     * 
     * @throws IllegalStateException If a problem arises when triying to start the
     *                              listening mechanism
     */
    public LongTestServiceEventListener() {
        serviceListener = new ServiceEventListener( DBMS.LONG_TEST_SVC_EVENT_TOPIC, 
                                                    new ServiceEventMessageListener(LongTestServiceEventNotificator.SERVICE_NAME));
    }

    /**
     * Adds the processor to process incoming events about 
     * test group finalization
     * 
     * @param processor The processor 
     */
    public void addTestGroupFinalizedProcessor(final TestGroupFinalizedProcessor processor) {
        serviceListener.addListener(LongTestServiceEventNotificator.EVENT_TEST_GROUP_FINISHED, 
                new ServiceEventHandler() {
                    public void eventReceived(String eventType, Serializable object) {
                        processor.testGroupFinalized( ((Integer) object).intValue() );
                    }
                });
    }
}
