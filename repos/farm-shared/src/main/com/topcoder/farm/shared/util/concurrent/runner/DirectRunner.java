/*
 * DirectRunner
 * 
 * Created 08/16/2006
 */
package com.topcoder.farm.shared.util.concurrent.runner;

/**
 * Simple Runner that runs tasks in the calling thread.
 * 
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class DirectRunner implements Runner {
    /**
     * Flag indication if the runner was stopped
     */
    private volatile boolean stopped = false;

    /**
     * @see Runner#run(Runnable)
     */
    public void run(Runnable task) {
        if (stopped) throw new IllegalStateException("The runner has been stopped");
        task.run();
    }

    /**
     * @see Runner#stop(boolean)
     */
    public void stop(boolean stopRunningTask) {
        stopped = true;
    }

    /**
     * @see Runner#stopAccepting()
     */
    public void stopAccepting() {
        stopped = true;
    }
}
