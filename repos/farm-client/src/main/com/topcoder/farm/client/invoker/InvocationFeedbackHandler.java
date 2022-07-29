/*
 * InvocationResultHandler
 * 
 * Created 08/15/2006
 */
package com.topcoder.farm.client.invoker;

import com.topcoder.farm.shared.invocation.InvocationFeedback;

/**
 * An InvocationFeedbackHandler is responsible for handling feedback received from
 * the farm. An InvocationFeedbackHandler will receive feedback only for the clients 
 * which the handler was registered for.<p>
 * 
 * To register a handler for an specific client. Users must invoke the method
 * {@link FarmFactory#configureHandler(String, InvocationFeedbackHandler)}.<p>
 * 
 * Handler must support concurrent access and can be registered for more that one client.<p>
 * 
 * @author Diego Belfer (mural)
 * @version $Id: $
 * @see FarmFactory#configureHandler(String, InvocationFeedbackHandler)
 */
public interface InvocationFeedbackHandler {
    
    /**
     * Invoked when feedback is received for the client for
     * which this handler was registered for
     *  
     * @param feedback The feedback received
     */
    void handleFeedback(InvocationFeedback feedback);
}
