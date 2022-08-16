/*
 * InvocationServices
 * 
 * Created 08/03/2006
 */
package com.topcoder.farm.controller.services;

import java.util.List;

import com.topcoder.farm.controller.processor.InvocationStatus;


/**
 * Service interface used by ProcessorManager to update
 * invocation information
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public interface InvocationServices {
    
    /**
     * Assigns the invocation with the given id to the processor
     *   
     * @param invocationId The id of the InvocationData object
     * @param processorId The processor assigned for the the invocation
     * 
     * @return The assigned invocation  or <code>null</code> if the assignation fails
     */
    public AssignedInvocation assignInvocationToProcessor(Long invocationId, String processorId);

    /**
     * Returns a list containing status information of all invocations that are assigned
     * to the given processor.
     * 
     * @param processorName The name of the processor
     * @return The list
     */
    public List<InvocationStatus> getAssignedInvocationsForProcessor(String processorName);

}
