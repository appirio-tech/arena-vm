/*
 * LongContestServicesTesterHandler
 * 
 * Created 09/14/2006
 */
package com.topcoder.server.ejb.TestServices;

import com.topcoder.server.common.LongSubmissionId;
import com.topcoder.server.farm.longtester.LongTesterInvoker.LongTesterHandler;
import com.topcoder.shared.util.logging.Logger;
import com.topcoder.services.tester.common.LongTestResults;
import com.topcoder.shared.common.LongRoundScores;
import com.topcoder.shared.common.ServicesConstants;

/**
 * @author Diego Belfer (mural)
 * @version $Id: LongContestServicesTesterHandler.java 70823 2008-05-27 20:49:33Z dbelfer $
 */
public class LongContestServicesTesterHandler implements LongTesterHandler {
    /**
     * Category for logging.
     */
    private final Logger log = Logger.getLogger(getClass());
    
    /**
     * @see com.topcoder.server.ejb.TestServices.LongTester.LongTesterHandler#reportLongSystemTestSubmissionResult(com.topcoder.server.ejb.TestServices.LongSubmissionId, long, com.topcoder.services.tester.common.LongTestResults)
     */
    public boolean reportLongSystemTestSubmissionResult(LongSubmissionId id, long testCaseId, LongTestResults testResults) {
        try {
            LongContestServicesLocator.getService().recordLongTestResult(id, testCaseId, ServicesConstants.LONG_SYSTEM_TEST_ACTION, testResults);
            return true;
        } catch (Exception e) {
            log.error("Could not report result to TestService, submission-id=["+id+"], test-case-id="+testCaseId, e);
            log.error(testResults);
            return false;
        }
    }

    public boolean reportLongTestSubmissionResult(LongSubmissionId id, long testCaseId, LongTestResults testResults) {
        try {
            LongContestServicesLocator.getService().recordLongTestResult(id, testCaseId, ServicesConstants.LONG_TEST_ACTION, testResults);
            return true;
        } catch (Exception e) {
            log.error("Could not report result to TestService, submission-id=["+id+"], test-case-id="+testCaseId, e);
            log.error(testResults);
            return false;
        }
    }

    public boolean reportCalculateSystemTestScoreSubmissionResult(int roundId, LongRoundScores lrr) {
        try {
            LongContestServicesLocator.getService().updateLongSystemTestFinalScores(new LongRoundScores[] {lrr});
            return true;
        } catch (Exception e) {
            log.error("Could not report system test score recalculation result to TestService, round="+lrr.getRoundID(), e);
            log.error(lrr);
            return false;
        }
    }

    /**
     * @see com.topcoder.server.longtester.LongTester.LongTesterHandler#reportCalculateTestScoreResult(int, com.topcoder.shared.common.LongRoundScores)
     */
    public boolean reportCalculateTestScoreResult(LongSubmissionId id, LongRoundScores lrr) {
        try {
            LongContestServicesLocator.getService().updateFinalScores(id, lrr);
            return true;
        } catch (Exception e) {
            log.error("Could not report score recalculation result to TestService, round="+lrr.getRoundID(), e);
            log.error(lrr);
            return false;
        }
    }

    /**
     * @see com.topcoder.server.farm.longtester.LongTesterInvoker.LongTesterHandler#reportLongTestSolutionResult(int, long, com.topcoder.services.tester.common.LongTestResults)
     */
    public boolean reportLongTestSolutionResult(int groupId, long testCaseId, LongTestResults testResults) {
        //We don't handle Solutions on TestServices
        return false;
    }
}
