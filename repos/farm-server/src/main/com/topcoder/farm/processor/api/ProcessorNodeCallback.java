/*
 * ProcessorNodeCallback
 * 
 * Created 07/20/2006
 */
package com.topcoder.farm.processor.api;

import com.topcoder.farm.managed.ManagedNode;
import com.topcoder.farm.satellite.SatelliteNodeCallback;

/**
 * Interface defining callback protocol, that allows the controller
 * to notify events to the Processor
 *  
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public interface ProcessorNodeCallback extends SatelliteNodeCallback, ManagedNode {
    
    /**
     * Makes the processor process the invocation
     * 
     * This  method is called by the controller when the processor
     * is available. A processor is available after calling setAsAvailable method
     * of the controller or after reporting a result to the Controller
     * 
     * @param request The invocation request assigned to the processor
     */
    void processInvocationRequest(ProcessorInvocationRequest request);

    /**
     * Makes the processor to register again with the controller
     * 
     */
    void forceReRegistration();
}
