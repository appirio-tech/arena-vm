/*
 * MTLockTest
 * 
 * Created 07/11/2006
 */
package com.topcoder.farm.shared.util.concurrent;

import junit.framework.TestCase;

/**
 * Multithread Test Case for class Lock
 *  
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class MTLockTest extends TestCase {
    private static final int LOCK_TIME = 500;
    private static final int MIN_TIME_TO_OBTAIN_LOCK = 100;
    
    private Lock lock;
    private Thread lckThread;
    private long startTS;
    private long endTS;
    private boolean twoLocks;

    public MTLockTest() {
    }
    
    protected void setUp() throws Exception {
        twoLocks = false;
        lock = new Lock();
        lckThread = buildTestThread();
    }

    private void startAll() throws InterruptedException {
        lckThread.start();
        Thread.sleep(MIN_TIME_TO_OBTAIN_LOCK);
    }
    
    protected void tearDown() throws Exception {
        lckThread.join();
    }

    private Thread buildTestThread() {
        return new Thread() {
            public void run() {
                lock.lock();
                if (twoLocks) lock.lock();
                try {
                    Thread.sleep(LOCK_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                lock.unlock();
                if (twoLocks) lock.unlock();
            };
        };
    }
   
    /**
     * Tests waiting. The lock attempt in this method sleep the thread until the other thread release
     * the lock
     */
    public void testLock() throws Exception {
        startAll();
        startTiming();
        lock.lock();
        endTiming();
        checkTimeGt(MIN_TIME_TO_OBTAIN_LOCK);
    }
    
    /**
     * Tests that unlock is not possible in a lock acquired by other thread
     */
    public void testUnlockThrowException() throws Exception {
        startAll();
        assertLock();
        try {
            lock.unlock();
            fail("expected exception");
        } catch (Exception e) {
        }
    }
    
    /**
     * Tests that it is not possible to unlock a lock acquired by other thread
     */
    public void testTryUnlockFails() throws Exception {
        startAll();
        assertLock();
        assertFalse(lock.tryToUnlock(1));
    }
    

    /**
     * Tests that after acquiring the lock two times, the lock is release after two unlocks
     */
    public void testReentrantLockReleaseLocks() throws Exception {
        twoLocks = true;
        startAll();
        assertLock();
        Thread.sleep(LOCK_TIME);
        assertNotLock();
    }

    /**
     * Tests that after acquiring the lock two times, the lock is release after two unlocks and
     * it can be taken by another thread
     */
    public void testReentrantLockReleaseAndOtherThreadCanTakeIt() throws Exception {
        twoLocks = true;
        startAll();
        assertLock();
        startTiming();
        lock.lock();
        endTiming();
        checkTimeGt(MIN_TIME_TO_OBTAIN_LOCK);
        assertLock();
    }
    
    /**
     * Tests that after acquiring the lock two times, the lock is release after two unlocks and
     * it can be taken by another thread
     */
    public void testThreeThreadLock() throws Exception {
        twoLocks = true;
        startAll();
        startTiming();
        assertLock();
        buildTestThread().start();
        buildTestThread().start();
        Thread.sleep(100);
        lock.lock();
        endTiming();
        checkTimeGt(LOCK_TIME*2+MIN_TIME_TO_OBTAIN_LOCK);
        assertLock();
    }
    
    private void checkTimeGt(long time) {
        assertTrue(time < (endTS - startTS));
    }

    private void endTiming() {
        this.endTS = System.currentTimeMillis();
    }

    private void startTiming() {
        this.startTS = System.currentTimeMillis();
    }

    private void assertNotLock() {
        assertFalse(lock.isLock());
    }
    
    private void assertLock() {
        assertTrue(lock.isLock());
    }
}
