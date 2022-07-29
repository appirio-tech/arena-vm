/*
 * ResettableTaskRunner
 * 
 * Created 03/28/2006
 */
package com.topcoder.shared.netCommon.resettabletask;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This class allows to start/stop all registered ResettableTimerTask at once.
 * Each task registered on this ResettableTaskRunner will run in its own
 * thread.
 *   
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ResettableTaskRunner {
    /**
     * This is where all task are registered
     */
    private Map tasks = new HashMap();

    /**
     * Flag used to indicate if the Runner is running or not
     */
    private boolean started;
    
    /**
    * This mutex provides synchronization when operating on tasks
    */
    private Object taskMutex = new Object();

    
    /**
     * Creates a new ResettableTaskRunner  
     */
    public ResettableTaskRunner() {
    }
    
    /**
     * Starts this ResettableTaskRunner.
     * Launchs one thread for each task registered. 
     * 
     * @throws IllegalStateException if this ResettableTaskRunner is already running
     */
    public void start() {
        synchronized (taskMutex) {
            if (started) {
                throw new IllegalStateException("ResettableTaskRunner is already running");
            };
            for (Iterator it = tasks.entrySet().iterator(); it.hasNext();) {
                Map.Entry entry = (Map.Entry) it.next();
                Thread thread = new Thread((Runnable) entry.getValue(), (String) entry.getKey());
                thread.setDaemon(true);
                thread.start();
            }
            started = true;
        }
    }
    
    /**
     * Stops this ResettableTaskRunner
     * Notifies all registered tasks that must stop.
     * 
     * @throws IllegalStateException if this ResettableTaskRunner is not running
     */
    public void stop() {
        synchronized (taskMutex) {
            if (!started) {
                throw new IllegalStateException("ResettableTaskRunner is not running");
            }
            for (Iterator it = tasks.entrySet().iterator(); it.hasNext();) {
                Map.Entry entry = (Map.Entry) it.next();
                ((ResettableTimerTask) entry.getValue()).stop();
            }
            started = false;
        }
    }
    
    /**
     * Determines whether this ResettableTaskRunner has been started.
     * A ResettableTaskRunner may be started/stopped using the methods
     * <code>start</code>/<code>stop</code>p
     * 
     * @return <code>true</code> If this ResettableTaskRunner is running
     *          <code>false</code> otherwise
     */
    public boolean isRunning() {
        return started;
    }
    
    /**
     * Registers a task to be runned when this ResettableTaskRunner starts
     * 
     * @param name Name of the registered tasks
     * @param task Task to be registered
     */
    public void registerTask(String name, ResettableTimerTask task) {
        synchronized (taskMutex) {
            if (started) {
                throw new IllegalStateException("ResettableTaskRunner already started");
            }
            tasks.put(name, task);
        }
    }
    
    /**
     * Resets the tasks registered with the given name.<p>
     * 
     * If the tasks is not registered, returns
     * 
     * @param name The name of the task
     */
    public void resetTask(String name) {
        ResettableTimerTask task = null;
        synchronized (taskMutex) {
            if (started) {
                throw new IllegalStateException("ResettableTaskRunner already started");
            }
            task = (ResettableTimerTask) tasks.get(name);
        }
        if (task != null) {
            task.reset();
        }
    }
}
