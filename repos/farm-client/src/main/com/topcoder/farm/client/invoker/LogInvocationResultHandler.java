/*
 * LogInvocationResultHandler
 * 
 * Created 08/15/2006
 */
package com.topcoder.farm.client.invoker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.topcoder.farm.controller.api.InvocationResponse;
import com.topcoder.farm.shared.invocation.InvocationFeedback;

/**
 * InvocationResultHandler that log results using log mechanims
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class LogInvocationResultHandler implements InvocationResultHandler, InvocationFeedbackHandler {
    /**
     * The log used for logging
     */
    private Log log;
    private String message;

    /**
     * Creates a new LogInvocationResultHandler that will used "._id" as suffix
     * for the category.
     * 
     * @param message The message to log.
     * @param id The id to add as suffix
     */
    public LogInvocationResultHandler(String message, String id) {
        this.log = LogFactory.getLog(getClass()+"._"+id);
        this.message = message;
    }

    /**
     * @see com.topcoder.farm.client.invoker.InvocationResultHandler#handleResult(com.topcoder.farm.controller.api.InvocationResponse)
     */
    public boolean handleResult(InvocationResponse response) {
        log.warn(message);
        log.warn("ID="+response.getRequestId());
        log.warn("RESULT");
        log.warn(response.getResult());
        return true;
    }

    public void handleFeedback(InvocationFeedback feedback) {
        log.warn(message);
        log.warn("ID="+feedback.getRequestId());
        log.warn("FEEDBACK");
        log.warn(feedback.getFeedbackData());        
    }
}
