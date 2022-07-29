/*
 * ResettableTimerTask
 * 
 * Created 03/28/2006
 */
package com.topcoder.shared.netCommon.resettabletask;

import com.topcoder.shared.util.concurrent.Waiter;


/**
 * Abstract Timer task that executes its abstract method doAction every <code>waitTime</code> 
 * milliseconds. The countdown timer can be reset invoking the method <code>reset</code>

 * @author Diego Belfer (Mural)
 * @version $Id$
 */
public abstract class ResettableTimerTask implements Runnable {
    
    /**
     * Time to wait (in Milliseconds) between invocations of the doAction method.
     */
    private long waitTime;
    
    /**
     * Flag to signal that task has been stopped 
     */
    private boolean stopped;
    
    /**
     * Flag to signal that countdown timer has been reset
     */
    private boolean reset;


    /**
     * Creates a new  ResettableTimerTask task with the specified waitTime
     * This task will execute the method <code>doAction</code> every <code>waitTime</code>
     * milliseconds if the countdown is not reset using the method <code>reset</code>
     * 
     * @param waitTime Time to wait (in Milliseconds) between invocations of the doAction method.
     */
    public ResettableTimerTask(long waitTime) {
        this.waitTime = waitTime;
    }

    /**
     * The action to be performed by this timer task.
     *
     * @return true If the timer task should be stopped. 
     */
    protected abstract boolean doAction();

    
    /**
     * Implementation of the main cycle, that will invoke the doAction method
     * every waitTime milliseconds
     */
    public final void run() {
        while (!stopped) {
            try {
                synchronized (this) {
                    reset = false;
                    Waiter waiter = new Waiter(waitTime, this);
                    while (!stopped && !reset && !waiter.elapsed()) {
                        waiter.await();
                    }
                }
                if (!stopped && !reset) {
                    boolean value = doAction();
                    synchronized (this) {
                        stopped |= value;
                    }
                }
            } catch (InterruptedException e) {
                stopped = true;
            } 
        }
        stopped = false;
    }

    /**
     * Resets the countdown timer
     */
    public synchronized void reset() {
        reset = true;
        notifyAll();
    }

    /**
     * Finalizes the task and the timer  
     */
    public synchronized void stop() {
        stopped = true;
        notifyAll();
    }
    
    /**
     * Sets the time to wait between invocations of the doAction method.
     * Resets the countdown timer. 
     *  
     * @param wt Time to wait (in milliseconds) between invocations of the doAction method.
     */
    public synchronized void setWaitTime(long wt) {
        waitTime = wt;
        reset = true;
        notifyAll();
    }
}