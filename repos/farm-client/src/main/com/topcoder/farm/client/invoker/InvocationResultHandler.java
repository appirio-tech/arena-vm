/*
 * InvocationResultHandler
 * 
 * Created 08/15/2006
 */
package com.topcoder.farm.client.invoker;

import com.topcoder.farm.controller.api.InvocationResponse;

/**
 * An InvocationResultHandler is responsible for handling results received from
 * the farm. An InvocationResultHandler will receive results only for the clients 
 * which the handler was registered for.<p>
 * 
 * To register a handler for an specific client. Users must invoke the method
 * {@link FarmFactory#configureHandler(String, InvocationResultHandler)}.<p>
 * 
 * Handler must support concurrent access and can be registered for more that one client.<p>
 * 
 * Note: The same response object may be notified more than one time. Handlers
 * must manage this situation.<p>
 * 
 *   
 * @author Diego Belfer (mural)
 * @version $Id$
 * @see FarmFactory#configureHandler(String, InvocationResultHandler)
 */
public interface InvocationResultHandler {
    
    /**
     * Invoked when an response is received for the client for
     * which this handler was registered for
     *  
     * @param response The response received
     * @return true if the result could be handled
     */
    public boolean handleResult(InvocationResponse response);
}
