/*
 * ExecutorServiceToRunnerAdapter
 *
 * Created 07/25/2006
 */
package com.topcoder.farm.shared.util.concurrent.runner;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ExecutorServiceToRunnerAdapter implements Runner {
    private Log log  = LogFactory.getLog(ExecutorServiceToRunnerAdapter.class);
    private ExecutorService service;

    public ExecutorServiceToRunnerAdapter(ExecutorService service) {
        this.service = service;
    }

    public void run(Runnable task) {
        service.execute(task);
        if (service instanceof ThreadPoolExecutor) {
            ThreadPoolExecutor executor = (ThreadPoolExecutor)service;
            if (log.isInfoEnabled()) {
                log.info("Pending tasks on executor [" + service.hashCode() + "]: "+(executor.getQueue().size())+" threads "+executor.getActiveCount()+"/"+executor.getMaximumPoolSize());
            }
        }
    }

    public void stop(boolean stopRunningTask) {
        if (stopRunningTask) {
            service.shutdownNow();
        } else {
            service.shutdown();
        }
    }

    public void stopAccepting() {
        service.shutdown();
    }
}
