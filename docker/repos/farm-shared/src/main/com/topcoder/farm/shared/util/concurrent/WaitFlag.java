/*
 * WaitFlag
 * 
 * Created 07/05/2006
 */
package com.topcoder.farm.shared.util.concurrent;

import com.topcoder.shared.util.concurrent.Waiter;

/**
 * This class implements a boolean condition object that can be set and cleared
 * as many times preferred
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class WaitFlag {
    /**
     * The object used to synchronize on
     */
    private final Object mutex = new Object();
    
    /**
     * The current value of the flag
     */
    private boolean value;

    /**
     * Creates a new unset WaitFlag
     */
    public WaitFlag() {
    }

    /**
     * Waits for this flag to be set.
     * It could wait forever if this flag is never set
     * 
     * @throws InterruptedException If the awaiting thread was interrupted 
     */
    public void await() throws InterruptedException {
        await(0);
    }
    
    /**
     * Waits for <code>ms</code> milliseconds to this flag to be set.
     * 
     * @param ms The max time to wait in milliseconds
     * @return true If the flag was set
     * @throws InterruptedException If the awaiting thread was interrupted
     */
    public boolean await(long ms) throws InterruptedException {
        Waiter waiter = new Waiter(ms, mutex);
        synchronized (mutex) {
            while (!value && !waiter.elapsed()) {
                waiter.await();
            }
            return value;
        }
    }
    
    /**
     * Sets this flag and wakes up all awaiting threads 
     */
    public void set() {
        synchronized (mutex) {
            value = true;
            mutex.notifyAll();
        }
    }

    /**
     * Clears the flag state. After calling this methods
     * all threads calling to await will block until <code>set</code>
     * be called.
     */
    public void clear() {
        synchronized (mutex) {
            value = false;
        }
    }
    
    /**
     * @return true is the flag is set.
     */
    public boolean isSet() {
        synchronized (mutex) {
            return value;
        }
    }
}
