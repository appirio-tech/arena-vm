/*
 * Lock
 * 
 * Created 07/11/2006
 */
package com.topcoder.farm.shared.util.concurrent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Simple implementation of a Lock object.
 * 
 * This lock provides more flexibility than using  
 * synchronized blocks
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class Lock {
    private Log log = LogFactory.getLog(Lock.class);
    
    /**
     * Current owner of the lock
     */
    private Thread owner = null;
    
    /**
     * Synchronization object
     */
    private final Object mutex = new Object();
   
    /**
     * Number of times the lock was acquired by the current owner thread
     */
    private int count = 0;
    
    /**
     * Creates a new Lock object
     */
    public Lock() {
    }

    /**
     * Acquires the lock.
     * 
     * If the lock is currently taken by another thread, 
     * the current thread becomes disabled until the lock could
     * be acquired. 
     * If the lock was already acquired by the current thread
     * the lock count increases. The current thread should unlock
     * as many time as it acquired the lock.
     * 
     * @return The lock count over this Lock Object for the calling thread
     *         0  if the lock could not be acquired (Due to interruption)
     */
    public int lock() {
        synchronized (mutex) {
            Thread currentThread = Thread.currentThread();
            if (owner == currentThread) {
                count++;
                return count;
            } else {
                while (owner != null) {
                    log.debug("sleeping thread");
                    try {
                        mutex.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw (IllegalStateException) new IllegalStateException("Lock acquisition interrupted!").initCause(e);
                    }
                }
                log.debug("waking up thread");
            }
            count = 1;
            owner = currentThread;
            return 1;
        }
    }

    /**
     * Tries to acquire the lock
     * 
     * If the lock is currently taken by another thread, 
     * it returns 0. 
     * If the lock was already acquired by the current thread
     * the lock count increases. The current thread should unlock
     * as many time as it acquired the lock.
     * 
     * @return The lock count over this Lock Object for the calling thread
     */
    public int tryLock() {
        synchronized (mutex) {
            Thread currentThread = Thread.currentThread();
            if (owner == currentThread) {
                count++;
                return count;
            } else if (owner != null) {
                return 0;
            }
            count = 1;
            owner = currentThread;
            return 1;
        }
    }
    
    /**
     * Releases the lock.<p>
     * 
     * If the current thread holds the lock, the lock count is
     * decreased. If the lock count after decreasing is equal 0 
     * the lock is fully released
     * 
     * @throws IllegalStateException If the current thread does not hold the lock
     */
    public void unlock() throws IllegalStateException {
        synchronized (mutex) {
            if (owner == Thread.currentThread()) {
                releaseLock();
                return;
            } 
        }
        throw new IllegalStateException("The Lock is not acquired by the current thread");
    }


    /**
     * Try to release the lock
     * 
     * If the current thread holds the lock, and the count is equal to the 
     * one indicated as argument, the lock count is decreased. 
     * If the lock count after decreasing is equal 0, the lock is released
     * 
     * @return true if the lock count was decreased
     */
    public boolean tryToUnlock(int lockCount) {
        synchronized (mutex) {
            if (owner == Thread.currentThread()) {
                if (lockCount == count) {
                    releaseLock();
                    return true;
                }
            }
        } 
        return false;
    }
    
    /**
     * Returns true if this lock is taken by some thread
     */
    public boolean isLock() {
        synchronized (mutex) {
            return owner != null;
        }
    }
    
    
    /**
     * Returns true if this lock is taken the calling thread
     */
    public boolean haveLock() {
        synchronized (mutex) {
            return owner == Thread.currentThread();
        }
    }
    
    /**
     * Release the lock.
     * This method must be called from a synchronize(mutex) block
     */
    private void releaseLock() {
        if (count == 0) return;
        if (--count == 0) {
            owner = null;
            mutex.notify();
        }
    }
}
