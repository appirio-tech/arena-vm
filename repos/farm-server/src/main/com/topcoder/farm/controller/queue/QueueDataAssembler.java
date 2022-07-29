/*
 * QueueDataAssembler
 * 
 * Created 08/06/2006
 */
package com.topcoder.farm.controller.queue;

import com.topcoder.farm.controller.model.InvocationData;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class QueueDataAssembler {

    /**
     * Creates a InvocationQueueHeaderData using the information provided in the 
     * InvocationData 
     * 
     * @param data The InvocationData instance containing the data
     *  
     * @return The new InvocationQueueHeaderData
     */
    public InvocationQueueHeaderData buildHeaderFor(InvocationData data) {
        InvocationQueueHeaderData header = new InvocationQueueHeaderData(data.getId(), data.getReceivedDate(), data.getDropDate(), data.getPriority());
        return header;
    }
    
    
    /**
     * Creates a InvocationQueueHeaderData using the information provided in the 
     * InvocationData 
     * 
     * @param data The InvocationData instance containing the data
     *  
     * @return The new InvocationQueueHeaderData
     */
    public InvocationQueueData buildQueueDataFor(InvocationData data) {
        return new InvocationQueueData(buildHeaderFor(data), data.getRequirements());
    }
}
