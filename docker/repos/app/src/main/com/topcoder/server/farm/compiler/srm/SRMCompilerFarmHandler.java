/*
 * SRMCompilerFarmHandler
 * 
 * Created 12/14/2006
 */
package com.topcoder.server.farm.compiler.srm;

import com.topcoder.farm.client.invoker.InvocationResultHandler;
import com.topcoder.farm.controller.api.InvocationResponse;
import com.topcoder.farm.shared.invocation.InvocationResult;
import com.topcoder.server.common.Submission;
import com.topcoder.server.farm.compiler.srm.SRMCompilerInvoker.SRMCompilerHandler;
import com.topcoder.shared.util.logging.Logger;

/**
 * Farm handler for SRMCompiler.<p>
 * 
 * Catches responses received from the farm, and notifies SRMCompilerHandler 
 * about compilation results.
 * 
 * @author Diego Belfer (mural)
 * @version $Id: SRMCompilerFarmHandler.java 70823 2008-05-27 20:49:33Z dbelfer $
 */
public class SRMCompilerFarmHandler implements InvocationResultHandler {
    /**
     * Category for logging.
     */
    private static final Logger log = Logger.getLogger(SRMCompilerFarmHandler.class);
    
    private SRMCompilerHandler compilerHandler;
    
    public SRMCompilerFarmHandler(SRMCompilerHandler compilerHandler) {
        this.compilerHandler = compilerHandler;
    }

    public boolean handleResult(InvocationResponse response) {
        Object id = response.getAttachment();
        boolean handled = true;
        if (id instanceof SRMCompilationId) {
            SRMCompilationId compilationId = (SRMCompilationId) id;
            InvocationResult result = response.getResult();
            if (!result.isExceptionThrown()) {
                Submission submission = (Submission) result.getReturnValue();
                handled = compilerHandler.reportSubmissionCompilationResult(compilationId, submission);
            } else {
                log.error("Exception thrown HANDLE THIS compilationId="+compilationId);
                log.error(result.getExceptionData());
            }
        } else {
            log.error("unknown identifier type="+id.getClass());
            handled = false;
        }
        return handled;
    }
}
