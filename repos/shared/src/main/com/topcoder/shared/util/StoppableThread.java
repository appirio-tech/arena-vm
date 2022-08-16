package com.topcoder.shared.util;

import com.topcoder.shared.util.logging.Logger;

/**
 * This class represents a thread that could be safely stopped.
 *
 * @author  Timur Zambalayev
 * @version  $Revision$
 */
public final class StoppableThread {
    private static final Logger logger = Logger.getLogger(StoppableThread.class);
    
    private final Client client;
    private final Thread thread;

    private boolean isStopped = true;

    /**
     * Creates a new thread with the specified client and name.
     *
     * @param   client      the client
     * @param   name        the name of this thread.
     */
    public StoppableThread(Client client, String name) {
        thread = new Thread(new STRunnable(), name);
        this.client = client;
    }

    /**
     * Returns a string representation of this thread.
     *
     * @return  a string representation of this thread.
     */
    public String toString() {
        return "StoppableThread " + thread;
    }

    boolean isStopped() {
        return isStopped;
    }

    /**
     * Causes this thread to begin execution.
     */
    public void start() {
        if (!isStopped) {
            throw new IllegalStateException("trying to start a started thread: " + thread);
        }
        isStopped = false;
        thread.start();
    }

    /**
     * Forces the thread to stop executing gracefully.
     *
     * @throws  InterruptedException    if another thread has interrupted the current thread.
     */
    public void stopThread() throws InterruptedException {
        if (isStopped) {
            return;
        }
        isStopped = true;
        if (Thread.currentThread() != thread) {
            thread.interrupt();
            join();
        }
    }

    void join(long millis) throws InterruptedException {
        thread.join(millis);
    }

    void join() throws InterruptedException {
        thread.join();
    }

    /**
     * The interface for <code>StoppableThread</code> clients.
     *
     * @author  Timur Zambalayev
     */
    public interface Client {

        /**
         * Does one cycle.
         *
         * @throws  InterruptedException    if another thread has interrupted the current thread.
         */
        void cycle() throws InterruptedException;

    }

    private class STRunnable implements Runnable {

        public void run() {
            try {
                while (!isStopped) {
                    try {
                        client.cycle();
                    } catch (InterruptedException e) {
                        throw e;
                    } catch (Throwable t) {
                        logger.error("", t);
                    }
                }
            } catch (InterruptedException e) {
            }
        }

    }

}
