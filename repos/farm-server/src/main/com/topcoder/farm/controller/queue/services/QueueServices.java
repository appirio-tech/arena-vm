/*
 * QueueServices
 * 
 * Created 08/01/2006
 */
package com.topcoder.farm.controller.queue.services;

import java.util.Iterator;
import java.util.Set;

import com.topcoder.farm.controller.model.ProcessorProperties;
import com.topcoder.farm.controller.queue.InvocationQueueData;

/**
 * Services used by the QueueManager 
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public interface QueueServices {
    
    /**
     * Returns a set containing all ProcessorProperties that are
     * currently active in the farm.
     * 
     * @return the set of active ProcessorProperties
     */
    Set<ProcessorProperties> getActiveProcessorProperties();

    /**
     * Returns an Iterator that iterates over InvocationQueueDatas of all
     * pending invocations in the farm that have not expired
     * 
     * @return The Iterator
     */
    Iterator<InvocationQueueData> getPendingInvocationHeaders();
    
    
    /**
     * Returns an Iterator that iterates over InvocationQueueDatas of all
     * pending invocations assigned to the given processor that are in the farm and 
     * have not expired
     * 
     * @param  processorName the name of the Processor
     * @return The Iterator
     */
    Iterator<InvocationQueueData> getPendingAssignedInvocationHeaders(String processorName);
    
   /**
    * Returns an Iterator that iterates over InvocationQueueDatas of all
    * pending invocations that were assigned to a processor and whose assignationTtl 
    * has been reached
    * 
    * @return The Iterator
    */
   Iterator<InvocationQueueData> getPendingAssignedInvocationHeaders();
}
