package com.topcoder.server.listener.util.concurrent;

/**
 * A reusable (restartable) barrier.
 */
public final class CountDown {

    private int count;

    /**
     * Creates a new <code>CountDown</code> with the given count value.
     *
     * @param   count   the count value.
     */
    public CountDown(int count) {
        restart(count);
    }

    /**
     * Restarts the barrier. It has the same effect as <code>new CountDown(count)</code>,
     * only you do not have to create a new instance.
     *
     * @param   count   the count value.
     */
    public void restart(int count) {
        this.count = count;
    }

    /**
     * Wait (possibly forever) until successful passage.
     *
     * @throws  java.lang.InterruptedException      if the current thread is interrupted.
     */
    public void acquire() throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        synchronized (this) {
            while (count > 0) {
                wait();
            }
        }
    }

    /**
     * Decrements the count. After the count reaches zero, all current acquires will pass.
     */
    public synchronized void release() {
        count--;
        if (count <= 0) {
            notifyAll();
        }
    }

}
