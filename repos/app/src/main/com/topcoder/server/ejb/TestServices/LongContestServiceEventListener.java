/*
 * LongContestServiceEventListener
 * 
 * Created 06/06/2006
 */
package com.topcoder.server.ejb.TestServices;

import java.io.Serializable;

import com.topcoder.shared.serviceevent.ServiceEventHandler;
import com.topcoder.shared.serviceevent.ServiceEventListener;
import com.topcoder.shared.serviceevent.ServiceEventMessageListener;
import com.topcoder.shared.util.DBMS;

/**
 * Listener process to receive events from the LongContestServices
 *  
 * @author Diego Belfer (mural)
 * @version $Id: LongContestServiceEventListener.java 74113 2008-12-29 19:34:43Z dbelfer $
 */
public class LongContestServiceEventListener  {
    public static final String SERVICE_NAME = "LongContestService";
    public static final String EVENT_REGISTER = "register";
    public static final String EVENT_SUBMIT = "submit";
    public static final String EVENT_SCORE = "score";
    public static final String EVENT_TEST_COMPLETED = "testCompleted";
    public static final String EVENT_SYSTEM_TEST_COMPLETED = "systemCompleted";
    public static final String EVENT_SYSTEM_TEST_ROUND_COMPLETED = "roundSystemCompleted";
    public static final String EVENT_OPENED = "opened";
    public static final String EVENT_SAVED = "saved";
    
    /**
     * The real listener
     */
    private ServiceEventListener serviceListener;
    private ServiceEventMessageListener messageListener;

    /**
     * Creates a new LongContestServiceEventListener
     * 
     * @throws IllegalStateException If a problem arises when triying to start the
     *                              listening mechanism
     */
    public LongContestServiceEventListener() {
        messageListener = new ServiceEventMessageListener(SERVICE_NAME);
        serviceListener = new ServiceEventListener( DBMS.LONG_CONTEST_SVC_EVENT_TOPIC, messageListener);
    }
    
    public void release() {
        serviceListener.release();
    }

    /**
     * Adds the processor to process incoming events about 
     * test group finalization
     * 
     * @param processor The processor 
     */
    public void addEventHandler(final Handler handler) {
        addEventHandler(messageListener, handler);
    }
    
    public static void addEventHandler(ServiceEventMessageListener l, final Handler handler) {
        l.addListener(EVENT_REGISTER, 
                new ServiceEventHandler() {
                    public void eventReceived(String eventType, Serializable object) {
                        Integer[] ids = (Integer[]) object;
                        handler.coderRegistered(ids[0].intValue(), ids[1].intValue());
                    }
                });
        l.addListener(EVENT_SUBMIT, 
                new ServiceEventHandler() {
                    public void eventReceived(String eventType, Serializable object) {
                        Object[] values = (Object[]) object;
                        handler.submissionMade(((Integer) values[0]).intValue(), ((Integer) values[1]).intValue(), ((Integer) values[2]).intValue(), ((Boolean) values[3]).booleanValue(), ((Integer) values[4]).intValue());
                    }
                });
        
        l.addListener(EVENT_TEST_COMPLETED, 
                new ServiceEventHandler() {
                    public void eventReceived(String eventType, Serializable object) {
                        Integer[] ids = (Integer[]) object;
                        handler.testCompleted(ids[0].intValue(), ids[1].intValue(), ids[2].intValue(), ids[3].intValue(), ids[4].intValue()==1);
                    }
                });

        l.addListener(EVENT_SYSTEM_TEST_COMPLETED, 
                new ServiceEventHandler() {
                    public void eventReceived(String eventType, Serializable object) {
                        Integer[] ids = (Integer[]) object;
                        handler.systemTestCompleted(ids[0].intValue(), ids[1].intValue(), ids[2].intValue(), ids[3].intValue());
                    }
                });

        l.addListener(EVENT_SCORE, 
                new ServiceEventHandler() {
                    public void eventReceived(String eventType, Serializable object) {
                        handler.overallScoreRecalculated((LongRoundOverallScore) object);
                    }
                });
        l.addListener(EVENT_OPENED, 
                new ServiceEventHandler() {
                    public void eventReceived(String eventType, Serializable object) {
                        Object[] values = (Object[]) object;
                        handler.componentOpened(((Integer) values[0]).intValue(), ((Integer) values[1]).intValue(), ((Integer) values[2]).intValue(), ((Long) values[3]).longValue());
                    }
                });
        l.addListener(EVENT_SAVED, 
                new ServiceEventHandler() {
                    public void eventReceived(String eventType, Serializable object) {
                        Object[] values = (Object[]) object;
                        handler.saved(((Integer) values[0]).intValue(), ((Integer) values[1]).intValue(), ((Integer) values[2]).intValue(), (String) values[3], ((Integer) values[4]).intValue());
                    }
                });
        
        l.addListener(EVENT_SYSTEM_TEST_ROUND_COMPLETED, 
                new ServiceEventHandler() {
                    public void eventReceived(String eventType, Serializable object) {
                        handler.roundSystemTestingCompleted(((Integer) object).intValue());
                    }
                });
    }
    
    public static interface Handler {
        void coderRegistered(int roundId, int coderId);
        void submissionMade(int roundId, int coderId, int componentId, boolean example, int submissionNumber);
        void overallScoreRecalculated(LongRoundOverallScore scores);
        void testCompleted(int roundId, int coder, int componentId, int submissionNumber, boolean example);
        /**
         * This method is called when all pending system tests for a given submissions have been executed.
         * This does not imply that the score has been recalculated.
         * 
         * @param roundId The round id
         * @param coder The coder Id
         * @param componentId The component Id
         * @param submissionNumber The submission Number.
         */
        void systemTestCompleted(int roundId, int coder, int componentId, int submissionNumber);
        
        /**
         * This method is called when all pending system tests for a given round have been executed.
         * When this notification is received, final score and rank have been calculated 
         * 
         * @param roundId The round id
         */
        void roundSystemTestingCompleted(int intValue);
        
        void componentOpened(int roundId, int coderId, int componentId, long openTime);
        void saved(int roundId, int coderId, int componentId, String source, int language);
    }
}
