/*
* Copyright (C) 2006-2013 TopCoder Inc., All Rights Reserved.
*/

/*
 * DequeuerObtainerImpl
 * 
 * Created 07/08/2006
 */
package com.topcoder.farm.controller.processor;

import com.topcoder.farm.controller.model.ProcessorProperties;
import com.topcoder.farm.controller.queue.InvocationIdDequeuer;
import com.topcoder.farm.controller.queue.QueueManager;

/**
 * <p>
 * Changes in version 1.0 (TC Competition Engine - Processor and Controller Handshake Change v1.0):
 * <ol>
 *      <li>Update {@link #getDequeuerFor(ProcessorProperties processorData)} method.</li>
 * </ol>
 * </p>
 * @author Diego Belfer (mural), TCSASSEMBLER
 * @version 1.0
 */
public class DequeuerObtainerImpl implements DequeuerObtainer {
    /**
     * The queueManager used to obtain dequeuers for each processor
     */
    private QueueManager queueManager;
    
    public DequeuerObtainerImpl(QueueManager queueManager) {
        this.queueManager = queueManager;
    }
    /**
     * <p>
     * get the dequeue.
     * </p>
     * @param processorData the processor data.
     */
    public InvocationIdDequeuer getDequeuerFor(ProcessorProperties processorData) {
        return queueManager.getQueueForProcessor(processorData);
    }
}
