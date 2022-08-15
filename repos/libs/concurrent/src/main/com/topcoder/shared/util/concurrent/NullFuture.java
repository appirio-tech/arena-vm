/*
 * NullFuture
 * 
 * Created Nov 2, 2007
 */
package com.topcoder.shared.util.concurrent;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Defines a empty asynchronous computation, which does not do any computation at all. It will always be immediately return
 * <code>null</code> as the result.
 *
 * @param <V> The result type returned by this Future's <tt>get</tt> method
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class NullFuture<V> implements Future<V>{
    /**
     * Cancels the asynchronous computation. This computation cannot be cancelled, since
     * it completes immediately.
     *
     * @param mayInterruptIfRunning an unused argument.
     * @return <code>false</code>
     */
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    /**
     * Gets the result of the asynchronous computation. It always return <code>null</code>.
     *
     * @return <code>null</code>
     */
    public V get() {
        return null;
    }

    /**
     * Gets the result of the asynchronous computation. It always return <code>null</code>.
     *
     * @param timeout an unused argument.
     * @param unit an unused argument.
     * @return <code>null</code>
     */
    public V get(long timeout, TimeUnit unit) {
        return null;
    }

    /**
     * Gets a flag indicating if the computation has been cancelled. This computation cannot be cancelled.
     *
     * @return <code>false</code>
     */
    public boolean isCancelled() {
        return false;
    }

    /**
     * Gets a flag indicating if the computation has been terminated. This computation is always completed.
     *
     * @return <code>true</code>
     */
    public boolean isDone() {
        return true;
    }
}
