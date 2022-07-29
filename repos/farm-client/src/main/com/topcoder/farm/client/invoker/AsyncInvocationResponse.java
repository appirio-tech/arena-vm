/*
 * AsyncInvocationResponse
 * 
 * Created 08/11/2006
 */
package com.topcoder.farm.client.invoker;

import com.topcoder.farm.controller.api.InvocationResponse;
import com.topcoder.farm.shared.util.concurrent.WaitFlag;

/**
 * AsyncInvocationResponse object is returned by the farm invoker
 * when a sync invocation is made. It allows to wait for a response
 * to an specific request made.
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class AsyncInvocationResponse {
    /**
     * Wait flag used for synchronization
     */
    private WaitFlag responseArrived = new WaitFlag();
    
    /**
     * Flag indicating that this AsyncInvocationResponse was cancelled
     */
    private boolean cancelled;
    
    /**
     * The exception (if any) was set to thrown when get is invoked
     */
    private FarmException exception;
    
    /**
     * The result (if any) that was set to return when get is invoked
     */
    private InvocationResponse result;
    
    
    /**
     * Creates a new AsyncInvocationResponse
     */
    public AsyncInvocationResponse() {
    }

    /**
     * Waits if necessary for the invocation to complete, and then
     * retrieves its result.
     * 
     * @return The InvocationResponse object which resulted from invocation
     * 
     * @throws InterruptedException If the thread was interrupted while waiting
     * @throws FarmException If an exception occurred during execution. (node disconnected, etc)
     */
    public InvocationResponse get() throws InterruptedException, FarmException {
        try {
            return get(0);
        } catch (TimeoutException e) {
            //Should not happen
            return null;
        }
    }
    
    /**
     * Waits if necessary for the invocation to complete, and then
     * retrieves its result.
     * 
     * @param timeoutMs Max time to wait in milliseconds.
     * @return The InvocationResponse object which resulted from invocation
     * 
     * @throws InterruptedException If the thread was interrupted while waiting
     * @throws TimeoutException If the wait time out
     * @throws FarmException If an exception occurred during execution. (NodeDisconnect, etc)
     * @throws CancellationException If cancel method was called
     */
    public InvocationResponse get(long timeoutMs) throws InterruptedException,
            TimeoutException, FarmException, CancellationException {
        if (!responseArrived.await(timeoutMs)) {
            throw new TimeoutException();
        }
        if (cancelled) {
            throw new CancellationException();
        }
        if (exception != null) {
            throw exception;
        }
        return result;
    }
    

    /**
     * Returns <tt>true</tt> if the response has been received or 
     * if an exception has been thrown while processing invocation or if 
     * it was cancelled
     * 
     * @return <tt>true</tt> if <code>get</code> methods exit immediately 
     */
    public boolean isDone() {
        return responseArrived.isSet();
    }
    
    
    /**
     * Sets the result for this AsyncInvocationResponse and
     * wakes up all threads waiting for result
     * 
     * @param response The resulting response to set
     * @return true if the result could be set.
     *         false if the result could not be set because this is cancelled 
     *               ,an exception was set or a result was already set 
     */
    boolean setResult(InvocationResponse response) {
        synchronized (responseArrived) {
            if (responseArrived.isSet()) return false;
            result = response;
            responseArrived.set();
            return true;
        }
    }
    
    /**
     * Sets an exception as result for this AsyncInvocationResponse and
     * wakes up all threads waiting for a result
     * 
     * @param e the FarmException to set
     * @return true if the exception could be set.
     *         false if the exception could not be set because this is cancelled 
     *               ,a result was set or an exception was already set
     */
    boolean setException(FarmException e) {
        synchronized (responseArrived) {
            if (responseArrived.isSet()) return false;
            exception = e;
            responseArrived.set();
            return true;
        }
    }
    
    /**
     * Cancels this AsyncInvocationResponse object.
     * 
     * @return true if this AsyncInvocationResponse could be cancelled
     *         false if it was already cancelled or  a result/exception is set  
     */
    public boolean cancel() {
        synchronized (responseArrived) {
            if (responseArrived.isSet()) return false;
            cancelled = true;
            responseArrived.set();
            return true;
        }
    }

    /**
     * @return true If this AsyncInvocationResponse was cancelled
     */
    public boolean isCancelled() {
        return responseArrived.isSet() && cancelled;
    }
}
