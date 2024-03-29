/*
* Copyright (C) - 2013 TopCoder Inc., All Rights Reserved.
*/

/*
 * ProcessorControllerNode
 * 
 * Created 07/20/2006
 */
package com.topcoder.farm.controller.api;

import com.topcoder.farm.controller.exception.NotAllowedToRegisterException;
import com.topcoder.farm.processor.api.ProcessorInvocationFeedback;
import com.topcoder.farm.processor.api.ProcessorInvocationResponse;
import com.topcoder.farm.processor.api.ProcessorNodeCallback;


/**
 * Interface exported to processors by the controller.
 * 
 * Processors use the methods defined in this interface to
 * interact with the controller
 *
 * <p>
 * Changes in version 1.0 (TC Competition Engine - Processor and Controller Handshake Change v1.0):
 * <ol>
 *      <li>Update {@link #registerProcessor(String groupId, ProcessorNodeCallback processor)} method.</li>
 *      <li>Update {@link #reRegisterProcessor(String, ProcessorNodeCallback, int)} method.</li>
 * </ol>
 * </p>
 * @author Diego Belfer (mural), TCSASSEMBLER
 * @version 1.0
 */
public interface ProcessorControllerNode extends SatelliteControllerNode {
    
    
    /**
     * Register the processor with this controller
     * 
     * @param groupId the group id of processor
     * @param processor The callback object for the processor 
     * @return the processor id with group id as prefix <code>groupId-number</code>
     * @throws NotAllowedToRegisterException if the controller rejects registration for the processor
     */
    public String registerProcessor(String groupId, ProcessorNodeCallback processor) throws NotAllowedToRegisterException;

    /**
     * Register a processor that was connected and disconnect abruptly
     * 
     * @param groupId the group id of processor
     * @param processor The callback object for the processor
     * @param currentLoad the current load the processor is running
     * @return the processor id with group id as prefix <code>groupId-number</code>
     * @throws NotAllowedToRegisterException if the controller rejects registration for the processor
     */
    public String reRegisterProcessor(String groupId, ProcessorNodeCallback processor, int currentLoad) throws NotAllowedToRegisterException;
    
    /**
     * Unregister the processor from this Controller 
     * 
     * @param id Id of the processor to unregister
     */
    public void unregisterProcessor(String id);

    /**
     * Returns the initialization data for the specified processor
     * 
     * @param id Id of the processor
     * 
     * @return The initialization data
     */
    public Object getProcessorInitializationData(String id);

    /**
     * Invoked by the processor to notify the controller that
     * a response was generate by the processor 
     * 
     * @param id Id of the processor that generated the response
     * @param response Response object
     */
    public void reportInvocationResult(String id, ProcessorInvocationResponse response);
    
    /**
     * Invoked by the processor to notify the controller that
     * an invocation feedback was generated by the processor 
     * 
     * @param id Id of the processor that generated the response
     * @param response Response object
     */
    public void reportInvocationFeedback(String id, ProcessorInvocationFeedback feedback);
    
    /**
     * Invoked by the processor to notify the controller if it
     * is available to received requests or not.
     * 
     * @param id Id of the processor that generated the response
     * @param available if true, controller will deliver tasks for processing
     */
    public void setAsAvailable(String id, boolean available);
}