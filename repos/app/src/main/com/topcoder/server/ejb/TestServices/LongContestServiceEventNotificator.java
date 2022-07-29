/*
 * LongContestServiceEventNotificator
 * 
 * Created 06/06/2006
 */
package com.topcoder.server.ejb.TestServices;

import com.topcoder.shared.util.logging.Logger;
import com.topcoder.shared.serviceevent.ServiceEventNotificator;
import com.topcoder.shared.util.DBMS;

/**
 * Notificator class used by the LongContestServiceBean to notify events.
 *  
 * @author Diego Belfer (Mural)
 * @version $Id: LongContestServiceEventNotificator.java 74113 2008-12-29 19:34:43Z dbelfer $
 */
public class LongContestServiceEventNotificator {
    private Logger logger = Logger.getLogger(LongContestServiceEventNotificator.class);

    /**
     * The real notificator 
     */
    private ServiceEventNotificator notificator =  null;

    /**
     * Constructs a new LongContestServiceEventNotificator.
     * 
     * @throws IllegalStateException If a problem arises when trying to start the
     *                               notification mechanism
     */
    public LongContestServiceEventNotificator() throws IllegalStateException {
        logger.info("Initializing LongContestServiceNotificator...");
        notificator = new ServiceEventNotificator(DBMS.LONG_CONTEST_SVC_EVENT_TOPIC, LongContestServiceEventListener.SERVICE_NAME);
        logger.info("Initialized LongContestServiceEventNotificator");
    }

    public void notifyCoderRegistered(int roundId, int coderId) {
        notificator.notifyEvent(LongContestServiceEventListener.EVENT_REGISTER, 
                new Integer[] {new Integer(roundId), new Integer(coderId)});
    }
    public void notifySubmissionMade(int roundId, int coderId, int componentId, boolean example, int submissionNumber) {
        notificator.notifyEvent(LongContestServiceEventListener.EVENT_SUBMIT, 
                new Object[] {new Integer(roundId), new Integer(coderId), new Integer(componentId), Boolean.valueOf(example), new Integer(submissionNumber)});
    }
    public void notifyTestCompleted(int roundId, int coderId, int componentId, int submissionNumber, boolean example) {
        notificator.notifyEvent(LongContestServiceEventListener.EVENT_TEST_COMPLETED, 
                new Integer[] {new Integer(roundId), new Integer(coderId), new Integer(componentId), new Integer(submissionNumber), new Integer(example?1:0)});
    }
    public void notifySystemTestCompleted(int roundId, int coderId, int componentId, int submissionNumber) {
        notificator.notifyEvent(LongContestServiceEventListener.EVENT_SYSTEM_TEST_COMPLETED, 
                new Integer[] {new Integer(roundId), new Integer(coderId), new Integer(componentId), new Integer(submissionNumber)});
    }
    public void notifyOverallScoreRecalculated(LongRoundOverallScore scores) {
        notificator.notifyEvent(LongContestServiceEventListener.EVENT_SCORE, scores);
    }
    public void componentOpened(int roundId, int coderId, int componentId, long openTime) {
        notificator.notifyEvent(LongContestServiceEventListener.EVENT_OPENED, 
                new Object[] {new Integer(roundId), new Integer(coderId), new Integer(componentId), new Long(openTime)});
    }
    public void notifySaved(int roundId, int coderId, int componentId, String programText, int languageId) {
        notificator.notifyEvent(LongContestServiceEventListener.EVENT_SAVED, 
                new Object[] {new Integer(roundId), new Integer(coderId), new Integer(componentId), programText, new Integer(languageId)});
    }
    public void notifyRoundSystemTestingCompleted(int roundId) {
        notificator.notifyEvent(LongContestServiceEventListener.EVENT_SYSTEM_TEST_ROUND_COMPLETED, new Integer(roundId));
    }
}
