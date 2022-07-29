/*
 * ReadWriteLock
 *
 * Created 03/08/2007
 */
package com.topcoder.farm.shared.util.concurrent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Simple implementation of a ReadWriteLock object.<p>
 *
 * A <tt>ReadWriteLock</tt> provides a pair of associated {@link
 * Lock locks}, one for read-only operations and one for writing.
 * Simultaneous {@link #readLock read locks} may be held by
 * multiple reader threads, so long as there are no writers.
 * {@link #writeLock Write locks} are exclusive and only one can be
 * locked at a time.
 *
 * This ReadWrite lock must be used carefully because only simple checks are done
 * to ensure safety.<p>
 *
 * Locks cannot be upgraded, if a read lock is held by a thread,
 * acquiring a write lock by the same thread will generated a deadlock.<p>
 *
 * Locks can be downgraded, having a writeLock allows to obtain a readLock, nevertheless
 * the write lock must be released calling unlock.<p>
 *
 * Concurrency Notes:
 * Instances of ReadWriteLock are thread safe and can be used from multiples thread, Nevertheless
 * {@link Lock} instances are not. Different threads must use different Lock instances.
 * A new Lock instance is easily obtained using readLock or writeLock.
 *
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ReadWriteLock {
    private static Log log  = LogFactory.getLog(ReadWriteLock.class);

    private int reading = 0;
    private int writing = 0;

    /**
     * Synchronization object
     */
    private final Object mutex = new Object();

    /**
     * Current thread holding a write lock
     */
    private Object currentWriter = null;

    /**
     * Creates a new ReadWriteLock object
     */
    public ReadWriteLock() {
    }


    /**
     * ReadLock object responsible for acquiring/releasing a read lock for the
     * ReadWriteLock.
     *
     * Instances of this class must not be used from multiple threads.
     */
    public class ReadLock implements Lock {
        /**
         * Number of times this lock was acquired
         */
        private volatile int count = 0;

        /**
         * Acquires a read lock.
         *
         * If a write lock is currently taken by another thread,
         * the current thread becomes disabled until the lock could
         * be acquired.
         *
         * If a read lock was already acquired by the current thread
         * the lock count increases. The current thread should unlock
         * as many time as it acquired the lock.
         *
         * @return The lock count over this Lock Object for the calling thread

         * @throws InterruptedException If the thread was interrupted while waiting
         *                              for lock acquisition
         */
        public int lock() throws InterruptedException {
            return waitLock(true);
        }

        /**
         * Acquires the lock only if it is free at the time of invocation.
         *
         * @return true if the lock could be acquired, false otherwise
         */
        public boolean tryLock() {
            try {
                return waitLock(false) != -1;
            } catch (InterruptedException e) {
                //never happens
                return false;
            }
        }

        private int waitLock(boolean mustWait) throws InterruptedException {
            synchronized (mutex) {
                while (true) {
                    if (count > 0 || writing == 0 || currentWriter  == Thread.currentThread()) {
                        reading++;
                        count++;
                        if (log.isTraceEnabled()) {
                            log.trace("readLock acquired on: "+ReadWriteLock.this);
                        }
                        return count;
                    }
                    if (!mustWait) {
                        if (log.isTraceEnabled()) {
                            log.trace("could get readLock on: "+ReadWriteLock.this);
                        }
                        return -1;
                    }
                    if (log.isTraceEnabled()) {
                        log.trace("waiting for readLock on: "+ReadWriteLock.this);
                    }
                    mutex.wait();
                }
            }
        }

        /**
         * Releases the lock.<p>
         *
         * If the current thread holds a lock, the lock count is
         * decreased. If the lock count after decreasing is equal 0
         * the lock is fully released
         *
         * @throws IllegalStateException If the current thread does not hold a lock
         */
        public void unlock() {
            synchronized (mutex) {
                if (count > 0) {
                    count--;
                    reading--;
                    if (log.isTraceEnabled()) {
                        log.trace("releasing readLock (still "+count+") on: "+ReadWriteLock.this );
                    }
                } else {
                    throw new IllegalStateException("Invalid lock state.");
                }
                if (reading == 0 && writing == 0) {
                    mutex.notify();
                }
            }
        }

        /**
         * Returns whether or not this lock is acquired
         *
         * @return true if the lock has a
         */
        public boolean isLocked() {
            return count > 0;
        }

        protected void finalize() throws Throwable {
            if (count > 0) {
                System.out.println("WARN!! releasing lock on finalize on readLock "+ReadWriteLock.this);
                synchronized (mutex) {
                    reading = reading - count;
                    count = 0;
                    if (reading == 0 && writing == 0) {
                        mutex.notifyAll();
                    }
                }
            }
        }
    }

    /**
     * WriteLock object responsible for acquiring/releasing a read lock for the
     * ReadWriteLock class;
     *
     * Instances of this class must not be used from multiple threads.
     */
    public class WriteLock implements Lock {
        /**
         * Number of times this lock was acquired
         */
        private volatile int count = 0;


        /**
         * Acquires a write lock.
         *
         * If a read or write lock is currently taken by another thread,
         * the current thread becomes disabled until the lock could
         * be acquired.
         *
         * If a write lock was already acquired by the current thread
         * the lock count increases. The current thread should unlock
         * as many time as it acquired the lock.
         *
         * IMPORTANT: If the thread holds a ReadLock for the same ReadWriteLock instance
         * a deadlock will occur.
         *
         * @return The lock count over this Lock Object for the calling thread

         * @throws InterruptedException If the thread was interrupted while waiting
         *                              for lock acquisition
         */
        public int lock() throws InterruptedException {
            return waitLock(true);
        }

        /**
         * Acquires the lock only if it can be acquired by this Lock object and the current thread.<p>
         *
         * If it is not possible to acquire the lock, it exists without waiting.<p>
         *
         * @return true if the lock could be acquired, false otherwise
         *
         * @see WriteLock#lock();
         */
        public boolean tryLock() {
            try {
                return waitLock(false) != -1;
            } catch (InterruptedException e) {
                //never happens
                return false;
            }
        }

        private int waitLock(boolean mustWait) throws InterruptedException {
            synchronized (mutex) {
                while (true) {
                    if (count > 0 || (reading == 0 && writing == 0) || currentWriter == Thread.currentThread()) {
                        if (writing == 0) {
                            currentWriter = Thread.currentThread();
                        }
                        writing++;
                        count++;
                        if (log.isTraceEnabled()) {
                            log.trace("writeLock acquired on: "+ReadWriteLock.this);
                        }
                        return count;
                    }
                    if (!mustWait) {
                        if (log.isTraceEnabled()) {
                            log.trace("could get writeLock on: "+ReadWriteLock.this);
                        }
                        return -1;
                    }
                    if (log.isTraceEnabled()) {
                        log.trace("waiting for writeLock on: "+ReadWriteLock.this);
                    }
                    mutex.wait();
                }
            }
        }


        /**
         * Releases the lock.<p>
         *
         * If the current thread holds the lock, the lock count is
         * decreased. If the lock count after decreasing is equal 0
         * the lock is fully released
         *
         * @throws IllegalStateException If the current thread does not hold a lock
         */
        public void unlock() {
            synchronized (mutex) {
                if (count > 0) {
                    count--;
                    writing--;
                    if (log.isTraceEnabled()) {
                        log.trace("releasing writeLock (still "+count+") on: "+ReadWriteLock.this );
                    }
                } else {
                    throw new IllegalStateException("Invalid lock state.");
                }
                if (writing == 0) {
                    currentWriter = null;
                    mutex.notify();
                }
            }
        }

        /**
         * Returns whether or not this lock is acquired
         *
         * @return true if the lock has a
         */
        public boolean isLocked() {
            return count > 0;
        }

        protected void finalize() throws Throwable {
            if (count > 0) {
                System.out.println("WARN!! releasing lock on finalize on writeLock "+ReadWriteLock.this);
                synchronized (mutex) {
                    writing = writing - count;
                    count = 0;
                    if (writing == 0) {
                        currentWriter = null;
                        mutex.notifyAll();
                    }
                }
            }
        }
    }


    /**
     * Acquires a ReadLock for this ReadWriteLock instance.
     *
     * You must lock the returned ReadLock object in order to actually acquire a read lock.
     *
     * @return The ReadLock object.
     */
    public ReadLock readLock() {
        return new ReadLock();
    }


    /**
     * Acquires a WriteLock for this ReadWriteLock instance.
     *
     * You must lock the returned WriteLock in order to actually acquire a write lock.
     *
     * @return The ReadLock object.
     */
    public WriteLock writeLock() {
        return new WriteLock();
    }

    /**
     * Shared interface for Read and Write Locks.<p>
     *
     */
    public interface Lock {

        /**
         * Acquires a lock.
         *
         * If a write lock is currently taken by another thread,
         * the current thread becomes disabled until the lock could
         * be acquired.
         *
         * If this lock is acquired, this increases the lock count of it.
         * The current thread should unlock as many time as it acquired the lock.
         *
         * @return The lock count over this Lock Object for the calling thread

         * @throws InterruptedException If the thread was interrupted while waiting
         *                              for lock acquisition
         */
        int lock() throws InterruptedException;

        /**
         * Releases the lock.<p>
         *
         * If the current thread holds a lock, the lock count is
         * decreased. If the lock count after decreasing is equal 0
         * the lock is fully released
         *
         * @throws IllegalStateException If the current thread does not hold a lock
         */
        void unlock();

        /**
         * Returns whether or not this lock is acquired
         *
         * @return true if the lock has a
         */
        boolean isLocked();

        /**
         * Acquires the lock only if it can be acquired by this Lock object and the current thread.<p>
         *
         * If it is not possible to acquire the lock, it exists without waiting.<p>
         *
         * @return true if the lock could be acquired, false otherwise
         *
         * @see Lock#lock();
         */
        boolean tryLock();
    }
}
