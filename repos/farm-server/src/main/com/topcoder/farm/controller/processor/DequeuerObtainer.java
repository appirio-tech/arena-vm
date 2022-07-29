/*
* Copyright (C) 2006-2013 TopCoder Inc., All Rights Reserved.
*/

/*
 * DequeuerObtainer
 * 
 * Created 07/08/2006
 */
package com.topcoder.farm.controller.processor;

import com.topcoder.farm.controller.model.ProcessorProperties;
import com.topcoder.farm.controller.queue.InvocationIdDequeuer;

/**
 * <p>
 * Changes in version 1.0 (TC Competition Engine - Processor and Controller Handshake Change v1.0):
 * <ol>
 *      <li>Update {@link #getDequeuerFor(ProcessorProperties processor)} method.</li>
 * </ol>
 * </p>
 * @author Diego Belfer (mural), TCSASSEMBLER
 * @version 1.0
 */
public interface DequeuerObtainer {
    /**
     * Returns a Dequeuer object that will provide invocation ids
     * for assigned to the give processor
     * 
     * @param processor The processor data
     * @return The dequeuer for the processor
     */
    public InvocationIdDequeuer getDequeuerFor(ProcessorProperties processor);
}
