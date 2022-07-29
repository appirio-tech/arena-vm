/*
* Copyright (C) 2006 - 2014 TopCoder Inc., All Rights Reserved.
*/
package com.topcoder.server.farm.longtester;

import com.topcoder.farm.client.invoker.InvocationResultHandler;
import com.topcoder.farm.controller.api.InvocationResponse;
import com.topcoder.farm.shared.invocation.InvocationResult;
import com.topcoder.server.common.LongSubmissionId;
import com.topcoder.server.ejb.TestServices.LongContestServicesTesterHandler;
import com.topcoder.server.ejb.TestServices.TestServicesLocator;
import com.topcoder.server.farm.longtester.LongTesterInvoker.LongTesterHandler;
import com.topcoder.shared.util.logging.Logger;
import com.topcoder.services.tester.common.LongTestResults;
import com.topcoder.shared.common.LongRoundScores;
import com.topcoder.shared.common.ServicesConstants;

/**
 * The long test result handler
 * 
 * <p>
 * Changes in version 1.1 (Fix New Arena Services Running in Arena VM v1.0) :
 * <ol>
 *      <li>Update {@link #handleResult(InvocationResponse response)} method.</li>
 * </ol>
 * </p>
 * @author Diego Belfer (mural), TCSASSEMBLER
 * @version 1.1
 */
public class LongTesterFarmHandler implements InvocationResultHandler {
    /**
     * Category for logging.
     */
    private static final Logger log = Logger.getLogger(LongTesterFarmHandler.class);
    
    private LongTesterHandler testerHandler;
    
    public LongTesterFarmHandler() {
    	this.testerHandler = new LongContestServicesTesterHandler();
    }
    
    public LongTesterFarmHandler(LongTesterHandler testerHandler) {
        this.testerHandler = testerHandler;
    }
    /**
     * Handle the result
     * @param response the invocation response
     * @return the long test result.
     */
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
                    try {
                        TestServicesLocator.getService().reportTestResult((int)longTestId.getTestCaseId(), testResults);
                        handled = true;
                    } catch (Exception e) {
                        log.error(e);
                        handled = false;
                    }
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
        	if (id == null) {
        		log.debug("No identifier returned for response " + response);
        	} else {
        		log.error("unknown identifier type="+id.getClass());
        	}
            handled = false;
        }
        return handled;
    }
}
