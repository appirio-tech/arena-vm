/*
 * ReadWriteLockTest
 *
 * Created 03/08/2007
 */
package com.topcoder.farm.shared.util.concurrent;

import com.topcoder.farm.shared.util.concurrent.ReadWriteLock.ReadLock;
import com.topcoder.farm.shared.util.concurrent.ReadWriteLock.WriteLock;
import com.topcoder.farm.test.util.MTTestCase;

/**
 * Test cases for {@link ReadWriteLock}
 *
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ReadWriteLockTest extends MTTestCase{

    private volatile boolean fail;

    protected void setUp() throws Exception {
        super.setUp();
        setWaitBetweenStarts(0);
        fail = false;
    }

    /**
     * Test that many read locks can be taken by the same thread
     */
    public void testReadLock() throws Exception {
        ReadWriteLock rwLock = new ReadWriteLock();
        ReadLock lck = rwLock.readLock();
        assertEquals(1, lck.lock());
        assertEquals(2, lck.lock());
        lck.unlock();
        assertEquals(2, lck.lock());
        lck.unlock();
        lck.unlock();
        try {
            lck.unlock();
            fail("Expected IllegalStateException");
        } catch (IllegalStateException e) {
            // OK
        }
    }

    /**
     * Test that many write locks can be taken by the same thread
     */
    public void testWriteLock() throws Exception {
        ReadWriteLock rwLock = new ReadWriteLock();
        WriteLock lck = rwLock.writeLock();
        assertEquals(1, lck.lock());
        assertEquals(2, lck.lock());
        lck.unlock();
        assertEquals(2, lck.lock());
        lck.unlock();
        lck.unlock();
        try {
            lck.unlock();
            fail("Expected IllegalStateException");
        } catch (IllegalStateException e) {
            // OK
        }
    }

    /**
     * Test that many read locks can be taken by the multiple threads
     */
    public void testManyReadLocks() throws Exception {
        final ReadWriteLock rwLock = new ReadWriteLock();
        for (int i = 0; i < 10; i++) {
            final int ii = i;
            run(new Runnable() {
                public void run() {
                    try {
                        ReadLock lock = rwLock.readLock();
                        for (int j =0; j <= ii; j++) {
                            lock.lock();
                        }
                        Thread.sleep(200);
                        for (int j =0; j <= ii; j++) {
                            lock.unlock();
                        }
                    } catch (Exception e) {
                        fail = true;
                    }
                }
            });
        }
        startTiming();
        startAllAndWait();
        long t = endTiming();
        assertFalse(fail);
        assertTrue(t < 400);
    }

    /**
     * Test that many write locks can be taken by only one thread at a time
     */
    public void testManyWriteLocks() throws Exception {
        final ReadWriteLock rwLock = new ReadWriteLock();
        for (int i = 0; i < 5; i++) {
            final int ii = i;
            run(new Runnable() {
                public void run() {
                    try {
                        WriteLock lock = rwLock.writeLock();
                        for (int j =0; j <= ii; j++) {
                            lock.lock();
                        }
                        Thread.sleep(200);
                        for (int j =0; j <= ii; j++) {
                            lock.unlock();
                        }
                    } catch (Exception e) {
                        fail = true;
                    }
                }
            });
        }
        startTiming();
        startAllAndWait();
        long t = endTiming();
        assertFalse(fail);
        assertTrue(t >= 1000);
    }

    /**
     * Tests that no write lock can be obtained by a thread if another thread
     * holds a read lock
     */
    public void testNoWriteLocksIfReadLock() throws Exception {
        final ReadWriteLock rwLock = new ReadWriteLock();
        run(new Runnable() {
            public void run() {
                try {
                    ReadLock lock = rwLock.readLock();
                    lock.lock();
                    Thread.sleep(300);
                    lock.unlock();
                } catch (Exception e) {
                    fail = true;
                }
            }
        });

        run(new Runnable() {
            public void run() {
                try {
                    WriteLock lock = rwLock.writeLock();
                    lock.lock();
                    Thread.sleep(200);
                    lock.unlock();
                } catch (Exception e) {
                    fail = true;
                }
            }
        });
        startTiming();
        startAllAndWait();
        long t = endTiming();
        assertFalse(fail);
        assertTrue(t >= 500 && t <= 700);
    }


    /**
     * Test that if thread holding a write lock, and it triesa read lock.
     * It is granted successfully.
     */
    public void testReadLockFromWrite() throws Exception {
        setWaitBetweenStarts(50);
        final ReadWriteLock rwLock = new ReadWriteLock();
        WriteLock lock = rwLock.writeLock();
        lock.lock();
        ReadLock rlock = rwLock.readLock();
        rlock.lock();
        rlock.unlock();
        lock.unlock();

        lock.lock();
        rlock.lock();
        lock.unlock();
        rlock.unlock();
    }


    /**
     * Test that if thread holding a write lock, tries any locks from other locks objects
     * it can obtain it.
     */
    public void testDifferentWriteLocksForSameThread() throws Exception {
        ReadWriteLock rwLock = new ReadWriteLock();
        WriteLock lock1 = rwLock.writeLock();
        WriteLock lock2 = rwLock.writeLock();
        ReadLock rlock1 = rwLock.readLock();
        ReadLock rlock2 = rwLock.readLock();
        lock1.lock();
        rlock1.lock();
        lock2.lock();
        rlock2.lock();

        lock1.unlock();
        lock2.unlock();
        rlock1.unlock();
        rlock2.unlock();
    }


    /**
     * Test that if a readLock holding a lock, releases it when it is garbage collected
     */
    public void testFinalizeReadLock() throws Exception {
        ReadWriteLock rwLock = new ReadWriteLock();
        acquireReadLock(rwLock.readLock());
        System.gc();
        System.runFinalization();
        Thread.sleep(100);
        System.gc();
        System.runFinalization();
        Thread.sleep(100);
        System.gc();
        System.runFinalization();

        ReadWriteLock.Lock rlock2 = rwLock.writeLock();
        assertTrue(rlock2.tryLock());
    }

    /**
     * Test that if a writeLock holding a lock, releases it when it is garbage collected
     */
    public void testFinalizeWriteLock() throws Exception {
        final ReadWriteLock rwLock = new ReadWriteLock();
        acquireReadLock(rwLock.writeLock());
        System.gc();
        System.runFinalization();
        Thread.sleep(100);
        System.gc();
        System.runFinalization();
        Thread.sleep(100);
        System.gc();
        System.runFinalization();

        Thread thread = new Thread() {
            public void run() {
                ReadWriteLock.Lock rlock2 = rwLock.writeLock();
                assertTrue(rlock2.tryLock());
            }
        };
        thread.start();
        thread.join();
    }

    private void acquireReadLock(ReadWriteLock.Lock lock) throws InterruptedException {
        lock.lock();
    }
}
