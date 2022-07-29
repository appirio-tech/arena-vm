package com.topcoder.shared.util;

import com.topcoder.shared.util.logging.Logger;

import java.util.*;

/**
 * @author unknown
 * @version  $Id$
 */
public class StageQueue {
    private static Logger log = Logger.getLogger(StageQueue.class);
    /**
     *
     */
    protected static List s_taskList = Collections.synchronizedList(new LinkedList());
    /**
     *
     */
    protected static ArrayList s_taskRunners = new ArrayList();

    /**
     *
     * @param r
     */
    public static void addTask(Runnable r) {
        if (s_taskRunners.isEmpty()) throw new RuntimeException("StageQueue not initialized.  Call start first");
        synchronized (s_taskList) {
            s_taskList.add(r);
            s_taskList.notifyAll();
        }
    }

    /**
     *
     */
    static class TaskRunner implements Runnable {
        /**
         *
         */
        protected boolean m_stopped = false;
        /**
         *
         */
        protected boolean waiting = false;

        /**
         *
         */
        public void run() {
            try {
                while (!m_stopped) {
                    waiting = true;
                    Runnable toRun = null;
                    synchronized (s_taskList) {
                        while (!m_stopped && s_taskList.isEmpty()) {
                            try {
                                s_taskList.wait();
                            } catch (InterruptedException ie) {
                                log.debug("Interrupted waiting for tasks", ie);
                            }
                        }
                        if (!s_taskList.isEmpty()) {
                            toRun = (Runnable) s_taskList.remove(0);
                        }
                    }
                    if (toRun != null) {
                        try {
                            waiting = false;
                            toRun.run();
                        } catch (Throwable t) {
                            log.error("Error occured while running task:", t);
                        }
                    }
                }
            } finally {
                waiting = false;
                log.debug("Exiting run");
            }
        }

        /**
         *
         */
        public void stop() {
            m_stopped = true;
        }

        /**
         *
         * @return
         */
        public boolean isReady() {
            return waiting;
        }
    }

    /**
     *
     * @return
     */
    public static int available() {
        int count = 0;
        for (int i = 0; i < s_taskRunners.size(); i++) {
            TaskRunner r = (TaskRunner) s_taskRunners.get(i);
            if (r.isReady()) count++;
        }
        return count - s_taskList.size();
    }

    /**
     *
     * @param numThreads
     */
    public static void start(int numThreads) {
        if (numThreads > s_taskRunners.size()) {
            for (int i = s_taskRunners.size(); i < numThreads; i++) {
                TaskRunner r = new TaskRunner();
                s_taskRunners.add(r);
                Thread t = new Thread(r, "StageQueueRunner." + i);
                t.setDaemon(true);
                t.start();
            }
        }
    }

    /**
     *
     */
    public static void stop() {
        for (int i = 0; i < s_taskRunners.size(); i++) {
            TaskRunner r = (TaskRunner) s_taskRunners.get(i);
            r.stop();
        }
        synchronized (s_taskList) {
            s_taskList.notifyAll();
        }
        s_taskRunners = new ArrayList();
    }
}
