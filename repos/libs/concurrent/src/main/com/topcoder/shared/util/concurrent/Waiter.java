/*
 * Waiter
 * 
 * Created Nov 19, 2007
 */
package com.topcoder.shared.util.concurrent;

/**
 * The Waiter class is a Helper class to simplify waiting on a object {@link Object#wait(long)}. 
 * It handles spurious wake ups and keeps track of the elapsed time since the first wait was issued.
 * 
 * For instance, since spurious wake ups may occur, each time we want to wait for an specific
 * amount of time, we should do something like this:
 * <pre>
 * synchronize (mutex) {
 *      //We should check for Long.MAX_VALUE. This code could overflow
 *      long maxTime = System.currentTimeMillis() + WAIT_TIME;
 *      long waitTime = maxTime - System.currentTimeMillis();
 *      while (!connected && (WAIT_TIME==0 || waitTime > 0)) {
 *          mutex.wait(waitTime);
 *          waitTime = maxTime - System.currentTimeMillis();
 *      }
 *      if (!connected) {
 *          throw new TimeoutException();
 *     }
 * }
 * </pre>
 * 
 * This code is not easy to follow and there is a missing check on it. <p>
 * 
 * Using this class the code can be reduced to <p>
 * <pre>
 * synchronize (mutex) {
 *      Waiter w = new Waiter(WAIT_TIME, mutex);
 *      while (!connected && !w.elapsed()) {
 *          w.await();
 *      }
 *      if (!connected) {
 *          throw new TimeoutException();
 *     }
 * }
 * </pre>
 * 
 * This class provides a Constructor that avoid timeout specification. It is only provided to be able to
 * handle wait() and wait(long) in a similar way.
 * 
 * 
 * The {@link Waiter#await()} method cannot be called from multiple threads concurrently.
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class Waiter {
    private final Object mutex;
    private long maxTime;
    private long elapse; 
    
    /**
     * Creates a waiter object that will use the provided mutex 
     * for waiting. The timeout is set to 0<p>
     * 
     * @param mutex The mutex object on which the wait will be issued
     */
    public Waiter(Object mutex) {
        this.mutex = mutex;
        init0();
    }
    
    private void init0() {
        maxTime = 0;
        elapse = 0;
    }

    /**
     * Creates a waiter object that will use the provided mutex for waiting.
     * The elapsed time indicates the max time the <code>await</code> method
     * will block after this instance creation or last reset.
     * 
     * @param elapse The max time to block after object creation or last reset.
     * @param mutex The object on which the wait will be issued.
     */
    public Waiter(long elapse, Object mutex) {
        this.mutex = mutex;
        if (elapse <= 0) {
            init0();
        } else {
            this.elapse = elapse;
            resolveMaxTime();
        }
    }

    private void resolveMaxTime() {
        if (Long.MAX_VALUE - elapse < System.currentTimeMillis()) {
            maxTime = Long.MAX_VALUE;
        } else {
            maxTime = System.currentTimeMillis() + elapse;
        }
    }
    /**
     * Returns true if the max time configured for this instance has elapsed. <p>
     * Elapsed time is considered since instance creation or last reset.
     * 
     * @return true if it has elapsed
     */
    public boolean elapsed() {
        return elapse != 0 && System.currentTimeMillis() >= maxTime; 
    }
    
    /**
     * Waits on the mutex until it is signaled, an spurious wake up occur or 
     * the max elapsed time has been reached.<p> 
     * 
     * The monitor lock must be held by the calling tread<p>
     * 
     * @throws InterruptedException if the thread was interrupted while waiting.
     */
    public void await() throws InterruptedException {
        if (elapse == 0) {
            mutex.wait();
        } else {
            long timeToWait = maxTime - System.currentTimeMillis();
            if (timeToWait > 0) {
                mutex.wait(timeToWait);
            }
        }
    }
    
    /**
     * Returns the remaining time this Waiter will block
     * when calling the await method.
     * 
     * @return The remaining time in ms.
     */
    public long getRemaining() {
        if (elapse == 0) {
            return Long.MAX_VALUE;
        } else {
            long time = maxTime - System.currentTimeMillis();
            return time > 0 ? time : 0;
        }
    }
    
    /**
     * Calls notifyAll on the mutex.<p>
     * 
     * Synchronization is done in the method.
     */
    public void synchNotifyAll() {
        synchronized (mutex) {
            mutex.notifyAll();
        }
    }
    
    /**
     * Calls notify on the mutex.<p>
     * 
     * Synchronization is done in the method.
     */
    public void synchNotify() {
        synchronized (mutex) {
            mutex.notifyAll();
        }
    }
    
    /**
     * Resets the wait time for this Waiter.<p>
     * 
     * After updating the remaining time, the mutex is signaled
     * using notifyAll method.<p>
     * 
     * Synchronization on the mutex is done by the method.
     */
    public void reset() {
        synchronized (mutex) {
            if (elapse != 0) {
                resolveMaxTime();
            }
            mutex.notifyAll();
        }
    }
}
