/*
 * Runner
 * 
 * Created 07/21/2006
 */
package com.topcoder.farm.shared.util.concurrent.runner;

/**
 * An object that runs submitted {@link Runnable} tasks. This
 * interface provides a way of decoupling task submission from the
 * mechanics of how each task will be run, including details of thread
 * use, scheduling, etc.  An <tt>Runner</tt> is normally used
 * instead of explicitly creating threads. For example, rather than
 * invoking <tt>new Thread(new(RunnableTask())).start()</tt> for each
 * of a set of tasks, you might use:
 * 
 *  runner.run(runnable1);
 *  runner.run(runnable2);
 *  
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public interface Runner {

    /**
     * The task may run in a new thread, in a pooled thread, 
     * or in the calling thread, at the discretion of the <tt>Runner</tt> 
     * implementation.
     *  
     * @param task Task to be run
     */
    void run(Runnable task);

    /**
     * Stops this Runner from accepting
     * new tasks. When this method is invoked
     * and the last task is run the runner will stop 
     */
    void stopAccepting();
    
    /**
     * Stops the runner, making the  currently running task the last one to run.
     * All references to tasks pending for execution in this runner are released.
     * 
     * @param stopRunningTask Indicates if the currently running task if any must be interrupted
     */
    void stop(boolean stopRunningTask);
}
