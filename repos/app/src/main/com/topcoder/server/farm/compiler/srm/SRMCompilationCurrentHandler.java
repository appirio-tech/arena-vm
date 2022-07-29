/*
 * SRMCompilationCurrentHandler
 * 
 * Created 12/14/2006
 */
package com.topcoder.server.farm.compiler.srm;

import com.topcoder.server.common.Submission;
import com.topcoder.server.ejb.TestServices.TestServicesLocator;
import com.topcoder.server.farm.compiler.srm.SRMCompilerInvoker.SRMCompilerHandler;
import com.topcoder.server.services.EventService;
import com.topcoder.shared.util.logging.Logger;

/**
 * SRM compilation result handler. 
 * 
 * Extracted from CompileMessageHandler.
 * 
 * @author Diego Belfer (mural)
 * @version $Id: SRMCompilationCurrentHandler.java 70823 2008-05-27 20:49:33Z dbelfer $
 */
public class SRMCompilationCurrentHandler implements SRMCompilerHandler {
    /**
     * Category for logging.
     */
    private final Logger log = Logger.getLogger(getClass());
    
    public boolean reportSubmissionCompilationResult(SRMCompilationId id, Submission sub) {
        try {
            log.info("Received SRM compilation result for compilationId=["+id+"] sucess="+sub.getCompileStatus());
            TestServicesLocator.getService().recordCompileStatus(sub);
            EventService.sendCompileResults(sub);
            return true;
        } catch (Exception e) {
            log.error("Could not report SRM compilation result to TestService, SRMCompilationId=["+id+"]", e);
            log.error(sub);
            return false;
        }
    }

}
