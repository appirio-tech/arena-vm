/*
 * InMemoryQueueServices
 * 
 * Created 08/01/2006
 */
package com.topcoder.farm.controller.queue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.topcoder.farm.controller.model.ProcessorProperties;
import com.topcoder.farm.controller.queue.services.QueueServices;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class InMemoryQueueServices implements QueueServices {
    private Set<ProcessorProperties> processorProperties = new HashSet<ProcessorProperties>();
    private List<InvocationQueueData> invocationData = new ArrayList<InvocationQueueData>();

    public Set<ProcessorProperties> getActiveProcessorProperties() {
        return processorProperties;
    }
    
    public void addProperties(ProcessorProperties properties) {
        processorProperties.add(properties);
    }

    public Iterator<InvocationQueueData> getPendingInvocationHeaders() {
        return invocationData.iterator();
    }
    
    public void addInvocationData(InvocationQueueData data) {
        invocationData.add(data);
    }

    public Iterator<InvocationQueueData> getPendingAssignedInvocationHeaders(String processorName) {
        return new ArrayList<InvocationQueueData>().iterator();
    }

    public Iterator<InvocationQueueData> getPendingAssignedInvocationHeaders() {
        return new ArrayList<InvocationQueueData>().iterator();
    }
}
