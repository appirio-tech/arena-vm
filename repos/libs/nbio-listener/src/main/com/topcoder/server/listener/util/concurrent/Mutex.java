package com.topcoder.server.listener.util.concurrent;

/**
 * A very simple mutual exclusion lock.
 */
public final class Mutex {

    private boolean inUse;

    /**
     * Creates a new mutex.
     */
    public Mutex() {
    }

    private boolean attempt(long millis) throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        synchronized (this) {
            if (!inUse) {
                inUse = true;
                return true;
            }
            if (millis <= 0) {
                return false;
            }
            throw new RuntimeException("not implemented");
        }
    }

    /**
     * Reports whether passed.
     *
     * @return  <code>true</code> if acquired.
     * @throws  java.lang.InterruptedException      if the current thread is interrupted.
     */
    public boolean attemptNow() throws InterruptedException {
        return attempt(0);
    }

    /**
     * Enables others to pass.
     */
    public synchronized void release() {
        inUse = false;
        notify();
    }

}
