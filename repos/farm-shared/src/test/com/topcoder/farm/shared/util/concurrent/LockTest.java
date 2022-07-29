/*
 * LockTest
 * 
 * Created 07/11/2006
 */
package com.topcoder.farm.shared.util.concurrent;

import com.topcoder.farm.shared.util.concurrent.Lock;

import junit.framework.TestCase;

/**
 * Test Case for class Lock
 *  
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class LockTest extends TestCase {
    private Lock lock;

    public LockTest() {
    }
    
    protected void setUp() throws Exception {
        lock = new Lock();
    }
   
    /**
     * Tests simple lock behavior, first lock returns 1 and one unlock release the lock
     */
    public void testLock() throws Exception {
        assertEquals(lock.lock(),1);
        assertLock();
        lock.unlock();
        assertNotLock();
    }
    
    /**
     * Tests reentrant lock, 2 locks 2 unlocks 
     */
    public void testMultiLock() throws Exception {
        assertEquals(lock.lock(),1);
        assertEquals(lock.lock(),2);
        assertLock();
        lock.unlock();
        assertLock();
        lock.unlock();
        assertNotLock();
    }

    /**
     * Tests than an exception is thrown if an unlock is attempted on a lock not acquired
     */
    public void testUnlockOnNotLockedThrowException() throws Exception {
        try {
            lock.unlock();
            fail("Expected exception");
        } catch (Exception e) {
        }
        assertNotLock();
    }

    /**
     * Tests than the more unlocks than locks throws exception
     */
    public void testTwoUnlockOnOneLockedThrowException() throws Exception {
        lock.lock();
        lock.unlock();
        try {
            lock.unlock();
            fail("Expected exception");
        } catch (Exception e) {
        }
        assertNotLock();
    }
    
    /**
     * Verifies that the tryUnlock works correctly for the normal flow
     */
    public void testTryUnlockValidReturnTrue() throws Exception {
        lock.lock();
        assertTrue(lock.tryToUnlock(1));
        assertNotLock();
    }

    /**
     * Verifies that the tryUnlock with an invalid lock count return false
     */
    public void testTryUnlockInvalidReturnFalse() throws Exception {
        lock.lock();
        assertFalse(lock.tryToUnlock(2));
        assertLock();
        assertTrue(lock.tryToUnlock(1));
        assertNotLock();
    }

    /**
     * Verifies that the calling tryUnlock 2 times with the same count, works the first time
     * but the seconds returns false
     */
    public void testTryUnlockDecreasesCountAndRelease() throws Exception {
        lock.lock();
        assertTrue(lock.tryToUnlock(1));
        assertFalse(lock.tryToUnlock(1));
        assertNotLock();
    }
    
    /**
     * Verifies that calling tryUnlock two times with the same count fails in the second attempt
     */
    public void testTryUnlockDecreasesCountAndRelease2() throws Exception {
        lock.lock();
        lock.lock();
        assertTrue(lock.tryToUnlock(2));
        assertTrue(lock.tryToUnlock(1));
        assertFalse(lock.tryToUnlock(1));
        assertNotLock();
    }

    private void assertNotLock() {
        assertFalse(lock.isLock());
    }
    
    private void assertLock() {
        assertTrue(lock.isLock());
    }

}
