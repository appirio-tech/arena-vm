/*
 * RunningThread
 * 
 * Created 18/08/2006
 */
package com.topcoder.farm.shared.util.concurrent.runner;

import java.util.LinkedList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

class RunningThread extends Thread {
    private Log log = LogFactory.getLog(RunningThread.class);
    
    /**
     * Indicates that this Runner must not run another task
     */
    boolean stopProcessing;
    
    /**
     * Indicates that this runner must not accept new tasks
     */
    boolean stopAccepting;
    LinkedList tasks;
    Object mutex; 
    
    public RunningThread(LinkedList tasks, Object mutex) {
        this.tasks = tasks;
        this.mutex = mutex;
    }

    public void run() {
        while (!stopProcessing) {
            Runnable task = null; 
            try {
                synchronized (mutex) {
                    while (tasks.size() == 0 && !stopAccepting && !stopProcessing) {
                        //JDK6 reviewed
                        mutex.wait();
                    }
                    if (tasks.size() == 0 || stopProcessing) {
                        break;
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
    }
}