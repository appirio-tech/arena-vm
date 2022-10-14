/*
 * TestInvoker
 * 
 * Created 10/23/2006
 */
package com.topcoder.server.ejb.TestServices.longtest;

import com.topcoder.server.common.LongSubmissionId;
import com.topcoder.server.ejb.TestServices.TestServicesLocator;
import com.topcoder.server.farm.longtester.LongTesterInvoker;
import com.topcoder.shared.util.logging.Logger;
import com.topcoder.services.tester.common.LongTestResults;
import com.topcoder.shared.common.LongRoundScores;

/**
 * @author Diego Belfer (mural)
 * @version $Id: TestInvoker.java 70823 2008-05-27 20:49:33Z dbelfer $
 */
public class TestInvoker {
    private static LongTesterInvoker instance;

    public static synchronized LongTesterInvoker getInstance() {
        if (instance == null) {
            instance = LongTesterInvoker.configure("MPSQAS", new LongTesterInvoker.LongTesterHandler() {
                private final Logger log = Logger.getLogger(getClass());
                
                public boolean reportLongTestSubmissionResult(LongSubmissionId id,
                        long testCaseId, LongTestResults testResults) {
                    // Not used
                    log.error("Unexpected event reported: reportLongTestSubmissionResult");
                    return false;
                }
            
                public boolean reportCalculateTestScoreResult(LongSubmissionId id, LongRoundScores lrr) {
                    // Not used
                    log.error("Unexpected event reported: reportCalculateTestScoreResult");
                    return false;
                }
                
                public boolean reportLongSystemTestSubmissionResult(LongSubmissionId id,
                        long testCaseId, LongTestResults testResults) {
                    //Not used
                    log.error("Unexpected event reported: reportLongSystemTestSubmissionResult");
                    return false;
                }
            
                public boolean reportCalculateSystemTestScoreSubmissionResult(int roundId,
                        LongRoundScores lrr) {
                    // Not used
                    log.error("Unexpected event reported: reportCalculateSystemTestScoreSubmissionResult");
                    return false;
                }

                public boolean reportLongTestSolutionResult(int groupId, long testCaseId, LongTestResults testResults) {
                    try {
                        TestServicesLocator.getService().reportTestResult((int) testCaseId, testResults);
                        return true;
                    } catch (Exception e) {
                        log.error("Cannot report test result to TestServices, TestCaseId=" + testCaseId);
                        return false;
                    } 
                }
            });
        }
        return instance;
    }
}
