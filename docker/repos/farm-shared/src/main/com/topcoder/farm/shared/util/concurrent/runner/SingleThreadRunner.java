/*
 * SingleThreadRunner
 * 
 * Created 07/21/2006
 */
package com.topcoder.farm.shared.util.concurrent.runner;

import java.util.LinkedList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Simplest implementation of a runner. 
 * 
 * Tasks are run in a background thread in the order
 * in which they were added.
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class SingleThreadRunner implements Runner {
    /**
     * Log Categoty for this class
     */
    private Log log = LogFactory.getLog(SingleThreadRunner.class);
    
    /**
     * List containing the tasks to run 
     */
    private LinkedList tasks = new LinkedList();
    
    /**
     * The background thread used to run the tasks
     */
    private RunningThread runningThread;
    
    /**
     * Mutex used for synchronization.
     */
    private Object mutex = new Object();

    /**
     * Flag indicating that the runner doesn't accept more tasks
     */
    private boolean noMoreTasks;

    /**
     * Creates a new SingleThreadRunner
     */
    public SingleThreadRunner() {
    }

    /**
     * @see Runner#stopAccepting()
     */
    public void stopAccepting() {
        synchronized (mutex) {
            noMoreTasks = true;
            if (runningThread != null && !runningThread.stopAccepting) {
                runningThread.stopAccepting = true;
                mutex.notify();
            }
        }
    }
    
    /**
     * @see Runner#stop(boolean)
     */
    public void stop(boolean stopRunningTask) {
        synchronized (mutex) {
            noMoreTasks = true;
            if (runningThread != null) {
                runningThread.stopProcessing = runningThread.stopAccepting = true;
                tasks.clear();
                if (stopRunningTask) {
                    runningThread.interrupt();
                } else {
                    mutex.notify();
                }
                runningThread = null;
                
            }
        }
    }

    /**
     * @see Runner#run(Runnable)
     */
    public void run(Runnable runnable) {
        synchronized (mutex) {
            if (noMoreTasks) {
                log.warn("Task scheduled while not accepting more task: " + runnable);
                throw new IllegalStateException("This runner doesn't accept more tasks");
            }
            if (runningThread == null) {
                startBgThread();
            }
            tasks.add(runnable);
            mutex.notifyAll();
        }
    }

    protected void finalize() throws Throwable {
        stopAccepting();
    }

    /**
     * Starts the background thread of this runner
     */
    private void startBgThread() {
        runningThread = new RunningThread(tasks, mutex);
        runningThread.start();
    }
}
