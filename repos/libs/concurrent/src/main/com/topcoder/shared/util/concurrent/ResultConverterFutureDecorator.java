package com.topcoder.shared.util.concurrent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Defines an abstract decorator class which converts a result object of an asynchronous computation into another object.
 * The decorator itself implements the <code>Future</code> interface.
 *
 * @param <T> the original result type of the future to be converted from.
 * @param <V> the final result type of the future to be converted to.
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public abstract class ResultConverterFutureDecorator<T, V> implements Future<V> {
    /** Represents the asynchronous computation whose result will be converted. */
    private Future<T> future;

    /**
     * Converts a result object into another object. This must be implemented by sub-classes to do the actual conversion.
     *
     * @param value the original result object to be converted.
     * @return the converted result object.
     * @throws InterruptedException if the current thread is interrupted while waiting.
     * @throws ExecutionException if the conversion threw an exception.
     */
    protected abstract V convertResult(T value) throws InterruptedException, ExecutionException;
    
    /**
     * Creates a new instance of <code>ResultConverterFutureDecorator</code> class. A <code>Future</code> object is given
     * whose computation result will be converted.
     *
     * @param future the asynchronous computation whose result will be converted.
     */
    public ResultConverterFutureDecorator(Future<T> future) {
        this.future = future;
    }

    /**
     * Cancels the wrapped asynchronous computation process. A flag indicating whether the in-process task should be
     * interrupted or not. If the process is not started yet, it will never be run.
     *
     * @param mayInterruptIfRunning a flag indicating if the in-process task should be interrupted or not.
     * @return <code>true</code> if the task can be cancelled; <code>false</code> otherwise.
     */
    public boolean cancel(boolean mayInterruptIfRunning) {
        return future.cancel(mayInterruptIfRunning);
    }

    /**
     * Gets the converted result object. This method waits for the wrapped computation to be completed if necessary.
     *
     * @return the result object which is converted from the wrapped computation result.
     * @throws InterruptedException if the current thread was interrupted while waiting.
     * @throws ExecutionException if the computation or conversion threw an exception.
     * @throws CancellationException if the computation was cancelled.
     */
    public V get() throws InterruptedException, ExecutionException {
        T value = future.get();
        return convertResult(value);
    }

    /**
     * Gets the converted result object. This method waits for at most the given time for the wrapped computation to
     * be completed.
     *
     * @param timeout the maximum time to wait for the computation.
     * @param unit the time unit of the <code>timeout</code> argument.
     * @return the result object which is converted from the wrapped computation result.
     * @throws InterruptedException if the current thread was interrupted while waiting.
     * @throws ExecutionException if the computation or conversion threw an exception.
     * @throws CancellationException if the computation was cancelled.
     * @throws TimeoutException if the computation cannot be completed within the given time.
     */
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        T value = future.get(timeout, unit);
        return convertResult(value);
    }

    /**
     * Gets a flag indicating if the computation has been cancelled.
     *
     * @return <code>true</code> if the computation was cancelled; <code>false</code> otherwise.
     */
    public boolean isCancelled() {
        return future.isCancelled();
    }

    /**
     * Gets a flag indicating if the computation has been terminated. The computation is terminated if the computation
     * is completed normally, throws an exception, or has been cancelled.
     *
     * @return <code>true</code> if the computation was terminated; <code>false</code> otherwise.
     */
    public boolean isDone() {
        return future.isDone();
    }
}
