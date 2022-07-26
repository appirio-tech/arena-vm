/*
 * ClientNodeCallback
 * 
 * Created 07/20/2006
 */
package com.topcoder.farm.client.node;

import com.topcoder.farm.controller.api.InvocationResponse;
import com.topcoder.farm.satellite.SatelliteNodeCallback;
import com.topcoder.farm.shared.invocation.InvocationFeedback;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public interface ClientNodeCallback extends SatelliteNodeCallback {
    
    /**
     * Callback method used by the controller to notify to the ClientNode
     * that a response is available 
     * 
     * @param response The invocation response available
     */
    void reportInvocationResult(InvocationResponse response);
    
    
    /**
     * Callback method used by the controller to notify to the ClientNode
     * that feeaback is available 
     * 
     * @param response The invocation response available
     */
    void reportInvocationFeedback(InvocationFeedback response);
}
