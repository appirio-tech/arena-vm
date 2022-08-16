/*
 * LongTesterFarmHandler
 * 
 * Created 09/14/2006
 */
package com.topcoder.server.farm.longtester;

import com.topcoder.farm.client.invoker.InvocationResultHandler;
import com.topcoder.farm.controller.api.InvocationResponse;
import com.topcoder.farm.shared.invocation.InvocationResult;
import com.topcoder.server.common.LongSubmissionId;
import com.topcoder.server.farm.longtester.LongTesterInvoker.LongTesterHandler;
import com.topcoder.shared.util.logging.Logger;
import com.topcoder.services.tester.common.LongTestResults;
import com.topcoder.shared.common.LongRoundScores;
import com.topcoder.shared.common.ServicesConstants;

/**
 * @author Diego Belfer (mural)
 * @version $Id: LongTesterFarmHandler.java 70823 2008-05-27 20:49:33Z dbelfer $
 */
public class LongTesterFarmHandler implements InvocationResultHandler {
    /**
     * Category for logging.
     */
    private static final Logger log = Logger.getLogger(LongTesterFarmHandler.class);
    
    private LongTesterHandler testerHandler;
    
    public LongTesterFarmHandler(LongTesterHandler testerHandler) {
        this.testerHandler = testerHandler;
    }

    public boolean handleResult(InvocationResponse response) {
        Object id = response.getAttachment();
        boolean handled = true;
        if (id instanceof LongTestId) {
            LongTestId longTestId = (LongTestId) id;
            InvocationResult result = response.getResult();
            if (!result.isExceptionThrown()) {
                LongTestResults testResults = (LongTestResults) result.getReturnValue();
                if (longTestId.getTestAction() == ServicesConstants.LONG_TEST_ACTION) {
                    handled = testerHandler.reportLongTestSubmissionResult((LongSubmissionId) longTestId.getTestedId(), longTestId.getTestCaseId(), testResults);
                } else if (longTestId.getTestAction() == ServicesConstants.LONG_SYSTEM_TEST_ACTION) {
                    handled = testerHandler.reportLongSystemTestSubmissionResult((LongSubmissionId) longTestId.getTestedId(), longTestId.getTestCaseId(), testResults);
                } else if (longTestId.getTestAction() == ServicesConstants.MPSQAS_TEST_ACTION) {
                    handled = testerHandler.reportLongTestSolutionResult(((Integer) longTestId.getTestedId()).intValue(), longTestId.getTestCaseId(), testResults);
                } else {
                    log.error("unknown test action="+longTestId.getTestAction());
                    handled = false;
                }
            } else {
                log.error("Exception thrown HANDLE THIS longTestId="+longTestId);
                log.error(result.getExceptionData());
            }
        } else if (id instanceof LongTestScoreId) {
            LongTestScoreId longTestId = (LongTestScoreId) id;
            InvocationResult result = response.getResult();
            if (!result.isExceptionThrown()) {
                LongRoundScores lrr = (LongRoundScores) result.getReturnValue();
                if (longTestId.getTestAction() == ServicesConstants.LONG_TEST_ACTION) {
                    handled = testerHandler.reportCalculateTestScoreResult((LongSubmissionId) longTestId.getId(), lrr);
                } else if (longTestId.getTestAction() == ServicesConstants.LONG_SYSTEM_TEST_ACTION) {
                    handled = testerHandler.reportCalculateSystemTestScoreSubmissionResult(((Number) longTestId.getId()).intValue(), lrr);
                } else {
                    log.error("unknown test action="+longTestId.getTestAction());
                    handled = false;
                }
            } else {
                log.error("Exception thrown HANDLE THIS longTestId="+longTestId);
                log.error(result.getExceptionData());
            }
        } else {
            log.error("unknown identifier type="+id.getClass());
            handled = false;
        }
        return handled;
    }
}
