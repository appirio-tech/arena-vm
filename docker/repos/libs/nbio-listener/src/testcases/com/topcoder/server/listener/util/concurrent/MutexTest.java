package com.topcoder.server.listener.util.concurrent;

import junit.framework.TestCase;

public final class MutexTest extends TestCase {

    public MutexTest(String name) {
        super(name);
    }

    public void testAttemptNow() {
        Mutex mutex = new Mutex();
        try {
            assertTrue(mutex.attemptNow());
            assertTrue(!mutex.attemptNow());
            assertTrue(!mutex.attemptNow());
            mutex.release();
            assertTrue(mutex.attemptNow());
            assertTrue(!mutex.attemptNow());
            assertTrue(!mutex.attemptNow());
        } catch (InterruptedException e) {
            fail();
        }
    }

}
