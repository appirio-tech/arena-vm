/*
 * ProcessorInvocationHandlerException
 * 
 * Created 25/09/2006
 */
package com.topcoder.farm.processor.api;

import com.topcoder.farm.shared.invocation.InvocationResult;

/**
 * This exception is thrown by ProcessorInvocationHandler, when a fatal
 * error occurred and more task should be handled by the processor.<p>
 *
 * This exception can be thrown specifying a result to send to the controller
 * before the processor shutdown. 
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ProcessorInvocationHandlerException extends Exception {
    private InvocationResult result;
    

    public ProcessorInvocationHandlerException() {
        super();
    }


    public ProcessorInvocationHandlerException(String message, Throwable cause) {
        super(message, cause);
    }


    public ProcessorInvocationHandlerException(String message) {
        super(message);
    }


    public ProcessorInvocationHandlerException(InvocationResult result) {
        super();
        this.result = result;
    }


    public ProcessorInvocationHandlerException(String message, Throwable cause, InvocationResult result) {
        super(message, cause);
        this.result = result;
    }


    public ProcessorInvocationHandlerException(String message, InvocationResult result) {
        super(message);
        this.result = result;
    }

    /**
     * Returns true if a result has been specified. This results should be sent to the controller
     * @return true if a result has been specified
     */
    public boolean isMustSendResult() {
        return result != null;
    }


    /**
     * @return The result that should be sent to the controller
     */
    public InvocationResult getResult() {
        return result;
    }
}
