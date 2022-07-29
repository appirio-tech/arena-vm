/*
 * SRMTesterFarmHandler
 * 
 * Created 01/05/2007
 */
package com.topcoder.server.farm.tester.srm;

import com.topcoder.farm.client.invoker.InvocationResultHandler;
import com.topcoder.farm.controller.api.InvocationResponse;
import com.topcoder.farm.shared.invocation.InvocationResult;
import com.topcoder.server.common.ChallengeAttributes;
import com.topcoder.server.common.SystemTestAttributes;
import com.topcoder.server.common.UserTestAttributes;
import com.topcoder.server.farm.tester.srm.SRMTesterInvoker.SRMTesterHandler;
import com.topcoder.shared.util.logging.Logger;

/**
 * Farm handler for SRMTester.<p>
 * 
 * Catches responses received from the farm, and notifies SRMTesterHandler 
 * about test results.
 * 
 * @author Diego Belfer (mural)
 * @version $Id: SRMTesterFarmHandler.java 70823 2008-05-27 20:49:33Z dbelfer $
 */
public class SRMTesterFarmHandler implements InvocationResultHandler {
    /**
     * Category for logging.
     */
    private static final Logger log = Logger.getLogger(SRMTesterFarmHandler.class);
    
    private SRMTesterHandler testerHandler;
    
    public SRMTesterFarmHandler(SRMTesterHandler testerHandler) {
        this.testerHandler = testerHandler;
    }

    public boolean handleResult(InvocationResponse response) {
        try {
            InvocationResult result = response.getResult();
            if (!result.isExceptionThrown()) {
                Object returnValue = result.getReturnValue();
                if (returnValue instanceof UserTestAttributes) {
                    testerHandler.reportUserTestResult((UserTestAttributes) returnValue);
                } else if (returnValue instanceof SystemTestAttributes) {
                    testerHandler.reportSystemTestResult((SystemTestAttributes) returnValue);
                } else if (returnValue instanceof ChallengeAttributes) {
                    testerHandler.reportChallengeTestResult((ChallengeAttributes) returnValue);
                } else {
                    log.error("Invalid return value received "+returnValue.getClass());
                    log.error(returnValue);
                }
                return true;
            } else {
                log.error("Exception thrown HANDLE THIS "+response.getRequestId());
                log.error(result.getExceptionData());
            }
        } catch (Exception e) {
            log.error("Exception thrown while processing result with id="+response.getRequestId(), e);
        }
        return false;
    }
}
