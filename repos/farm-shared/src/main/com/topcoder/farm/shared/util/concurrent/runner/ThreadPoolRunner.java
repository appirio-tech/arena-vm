/*
 * ThreadPoolRunner
 *
 * Created 08/08/2006
 */
package com.topcoder.farm.shared.util.concurrent.runner;

import java.util.Iterator;
import java.util.LinkedList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.topcoder.shared.util.concurrent.Waiter;

/**
 * Runner that uses a ThreadPool to run tasks
 *
 * Tasks are run in a background threads and peek from the queue
 * in the order in which they were added.
 *
 * This pool creates new threads as needed. The created threads are not
 * destroyed until the runner is stopped.
 *
 * Java 1.4 compatible. Use ExecutorServiceToRunnerAdapter if Java 1.5 is available
 *
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ThreadPoolRunner implements Runner {
    /**
     * Log Category for this class
     */
    private Log log = LogFactory.getLog(ThreadPoolRunner.class);

    /**
     * List containing the tasks to run
     */
    private LinkedList tasks = new LinkedList();

    /**
     * List containing the tasks to run
     */
    private LinkedList threads = new LinkedList();

    /**
     * Maximum number of threads the pool will create
     */
    private int maxThreads = 0;
    
    /**
     * Minimum number of threads the pool will contain.
     * Idle threads are released after maxIdleTime if
     * the number of threads exceeds the minThreads value
     */
    private int minThreads = 0;

    /**
     * Number of threads that are waiting for tasks assignment
     */
    private int availableThreads = 0;
    
    /**
     * Maximum idle time for threads waiting for a task. Idle threads
     * are release if the number of threads is greater than maxThreads
     */
    private long maxIdleTime = 0;

    /**
     * Mutex used for synchronization.
     */
    private Object mutex = new Object();

    /**
     * Indicates that this runner must not accept new tasks
     */
    private boolean stopAccepting = false;

    /**
     * Indicates that this Runner must not run another task
     */
    private boolean stopProcessing =  false;

    /**
     * The poolName, used to generate Threads' name
     */
    private String poolName;

    /**
     * Creates a new SingleThreadRunner
     *
     * @param poolName The poolName, used to generate Threads' name
     * @param maxThreads The maximum numbers of threads this pool will create
     */
    public ThreadPoolRunner(String poolName, int maxThreads) {
        this(poolName, maxThreads, maxThreads, 0);
    }
    
    /**
     * Creates a new SingleThreadRunner
     *
     * @param poolName The poolName, used to generate Threads' name
     * @param minThreads The minimum number of threads this pool will create. 
     * @param maxThreads The maximum number of threads this pool will create
     * @param idleTime Idle time before releasing threads.
     */
    public ThreadPoolRunner(String poolName, int minThreads, int maxThreads, long idleTime) {
        this.maxThreads = maxThreads;
        this.poolName = poolName;
        this.minThreads = minThreads;
        this.maxIdleTime = idleTime;
    }

    /**
     * @see Runner#stopAccepting()
     */
    public void stopAccepting() {
        if (!stopAccepting) {
            synchronized (mutex) {
                stopAccepting = true;
                mutex.notifyAll();
            }
        }
    }

    /**
     * @see Runner#stop(boolean)
     */
    public void stop(boolean stopRunningTask) {
        if (threads.size() != 0) {
            synchronized (mutex) {
                stopProcessing = stopAccepting = true;
                tasks.clear();
                if (stopRunningTask) {
                    interruptThreads();
                } else {
                    mutex.notifyAll();
                }
                threads.clear();

            }
        }
    }

    /**
     * Blocks until all tasks are finished. Before calling this method
     * stop or stopAceptting must be called.
     */
    public void awaitAllTaskTermination() {
        synchronized (mutex) {
            if (!stopAccepting) {
                throw new IllegalStateException("The runner must be stopped or not receiving more tasks to invoke this method");
            }
            while (availableThreads > 0 || tasks.size() > 0) {
                try {
                    //JDK 6 reviewed
                    mutex.wait(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }

    /**
     * Interrupts all threads
     */
    private void interruptThreads() {
        for (Iterator it = threads.iterator(); it.hasNext();) {
            RunningThread thread = (RunningThread) it.next();
            thread.interrupt();
        }
    }

    /**
     * @see Runner#run(Runnable)
     */
    public void run(Runnable runnable) {
        synchronized (mutex) {
            if (stopAccepting) {
                log.warn("Task schedule while not accepting more task: " + runnable);
                throw new IllegalStateException("This runner doesn't accept more tasks");
            }
            if (availableThreads == 0 && threads.size() < maxThreads) {
                startNewThread();
            }
            tasks.add(runnable);
            if (log.isDebugEnabled()) {
                log.debug("Pending tasks on pool [" + poolName + "]: "+tasks.size()+" threads "+availableThreads+"/"+threads.size()+"/"+maxThreads);
            }
            mutex.notify();
        }
    }

    /**
     * @return the number of threads the pool contains
     */
    public int getPoolSize() {
        synchronized (mutex) {
            return threads.size();
        }
    }

    /**
     * Starts the background thread of this runner
     */
    private void startNewThread() {
        RunningThread runningThread = new RunningThread(poolName+"["+threads.size()+"]");
        threads.add(runningThread);
        runningThread.start();
    }

    protected void finalize() throws Throwable {
        stopAccepting();
    }

    /**
     * Thread class used to run tasks
     */
    private class RunningThread extends Thread {

        public RunningThread(String name) {
            super(name);
            this.setDaemon(true);
        }

        public void run() {
            while (!stopProcessing) {
                Runnable task = null;
                try {
                    synchronized (mutex) {
                        availableThreads++;
                        try {
                            Waiter waiter = new Waiter(maxIdleTime,mutex);
                            while (tasks.size() == 0 && !stopAccepting && !stopProcessing && !waiter.elapsed()) {
                                waiter.await();
                            }
                            if (stopProcessing || (stopAccepting && tasks.size() == 0)) {
                                break;
                            }
                            if (tasks.size() == 0) {
                                if (threads.size() > minThreads) {
                                    threads.remove(this);
                                    return;
                                }
                                continue;
                            }
                        } finally {
                            availableThreads--;
                        }
						task = (Runnable) tasks.removeFirst();
                    }
					task.run();
                } catch (InterruptedException e) {
                    break;
                } catch (Exception e) {
                    log.error("Exception thrown by running task ",e);
                }
            }
            log.debug("Exit thread "+getName());
        }
    }
}
