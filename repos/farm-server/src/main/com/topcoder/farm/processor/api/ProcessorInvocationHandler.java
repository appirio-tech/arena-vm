/*
 * ProcessorInvocationHandler
 * 
 * Created 08/18/2006
 */
package com.topcoder.farm.processor.api;

import com.topcoder.farm.shared.invocation.Invocation;
import com.topcoder.farm.shared.invocation.InvocationFeedbackPublisher;
import com.topcoder.farm.shared.invocation.InvocationResult;

/**
 * ProcessorNode will call the ProcessorInvocationHandler set
 * every time an invocation request arrives to the Node
 *
 * Only one thread at the time will call the handle method of this interface, 
 * therefore is not required for implementors of this interface to be thread-safe
 * is no other thread will use the instance  
 * 
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public interface ProcessorInvocationHandler {

    /**
     * This method gets called every time an Invocation requests
     * arrives to the ProcessorNode on which this ProcessorInvocationHandler 
     * was set.
     * 
     *  
     * @param invocation The invocation to handle
     * @return The result of the invocation
     * @throws ProcessorInvocationHandlerException if a fatal error occurred and the processor should be 
     *                                             restarted
     */
    public InvocationResult handle(InvocationFeedbackPublisher feedbackPublisher, Invocation invocation) throws ProcessorInvocationHandlerException;
}