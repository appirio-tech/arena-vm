/*
 * FutureImpl
 * 
 * Created Oct 25, 2007
 */
package com.topcoder.shared.util.concurrent;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Simple Future implementation which allows setting result of 
 * an asynchronous execution and releasing threads waiting on <code>get</code> methods.
 * 
 * This simple implementation will return <code>false</code> on cancel method since 
 * it does not know anything about the process generating the result for this future.
 * A hook method <code>bareCancel</code> is provided to allow extending classes to cancel the related process.
 * 
 * Another hook method <code>futureReady</code> is provided to allow extending classes to do any specific task
 * when the future becomes ready. Ready means here, that the get method won't block.
 *  
 * @author Diego Belfer (Mural)
 * @version $Id$
 * @param <T> The result type returned by {@link FutureImpl} gets method.
 */
public class FutureImpl<T> implements Future<T> {
    private static final int RESULT_SET = 3;
    private static final int FAILED = 2;
    private static final int CANCELLED = 1;
    
    private volatile int status;
    private Object mutex = new Object();
    private T value;
    private Exception exception;
    
    /**
     * Creates a new FutureImpl
     */
    public FutureImpl() {
    }
    
    
    public boolean cancel(boolean mayInterruptIfRunning) {
        synchronized (mutex) {
            if (status != 0) {
                return false;
            }
            status = CANCELLED;
            mutex.notifyAll();
        }
        boolean result = bareCancel();
        instanceReady();
        return result;
    }
    
    /**
     * Sets the value to be returned by the future. If any thread is waiting 
     * on a get method, it will be awaked. <p>
     * 
     * The value is only set if no other value was previously set, no exception was set,
     * neither the future was canceled.
     * 
     * @param value The value to set.
     * @return true if the value of the result is set; false otherwise.
     */
    public boolean setValue(T value) {
        synchronized (mutex) {
            if (status != 0) {
                return false;
            }
            this.value = value;
            status = RESULT_SET;
            mutex.notifyAll();
        }
        instanceReady();
        return true;
    }
    
    /**
     * Sets the exception to be returned by the future. If any thread is waiting 
     * on a get method, it will be awaked. <p>
     * 
     * The exception is only set if no other value was previously set, no exception was set,
     * neither the future was canceled.<p>
     * 
     * This exception will be the cause of the {@link ExecutionException} thrown by the get methods.
     * 
     * @param e The exception to set.
     */
    public void setException(Exception e) {
        synchronized (mutex) {
            if (status != 0) {
                return;
            }
            this.exception = e;
            status = FAILED;
            mutex.notifyAll();
        }
        instanceReady();
    }

    public T get() throws InterruptedException, ExecutionException {
        try {
            return get(0, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            // Never timeout
            return null;
        }
    }

    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        waitForStatusChange(unit.toMillis(timeout));
        if (status == RESULT_SET) {
            return value;
        } else if (status == CANCELLED) {
            throw new CancellationException("Cancelled!");
        } else if (status == FAILED) {
            throw new ExecutionException("The execution thrown an exception", exception);
        } else {
            throw new TimeoutException("Timeout reached!");
        }
    }

    private void waitForStatusChange(long timeout) throws InterruptedException {
        if (timeout == 0) {
            synchronized (mutex) {
                while (status == 0) {
                    mutex.wait();
                }
            }
        } else {
            long finalTime = Long.MAX_VALUE;
            if (timeout < finalTime - System.currentTimeMillis()) {
                finalTime = System.currentTimeMillis() + timeout;
            }
            synchronized (mutex) {
                long waitTime = finalTime - System.currentTimeMillis();
                while (status == 0 && waitTime > 0) {
                    mutex.wait(waitTime);
                    waitTime = finalTime - System.currentTimeMillis();
                }
            }
        }
    }

    public boolean isCancelled() {
        return status == CANCELLED;
    }

    /**
     * Returns true if a result is available.<p>
     * When isDone is true gets method won't block the calling thread.
     */
    public boolean isDone() {
        return status != 0;
    }
    
    protected void instanceReady() {
    }
    
    protected boolean bareCancel() {
        return false;
    }
}