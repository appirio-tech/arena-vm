package com.topcoder.shared.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

public final class SerialIntGeneratorTest extends TestCase {

    public SerialIntGeneratorTest(String name) {
        super(name);
    }

    public void testDifferent() {
        SerialIntGenerator generator = new SerialIntGenerator();
        Set set = new HashSet();
        for (long i = 0; i < 10; i++) {
            assertTrue(set.add(new Integer(generator.next())));
        }
    }

    public void testThreads() {
        int n = 5;
        SerialIntGenerator generator = new SerialIntGenerator();
        Set set = Collections.synchronizedSet(new HashSet());
        TestThread thread[] = new TestThread[n];
        Object lock = new Object();
        for (int i = 0; i < n; i++) {
            thread[i] = new TestThread(generator, set, lock);
        }
        synchronized (lock) {
            while (TestThread.getCount() > 0) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    fail();
                }
            }
        }
    }

    public void testLimits() {
        SerialIntGenerator generator = new SerialIntGenerator(Integer.MAX_VALUE);
        generator.next();
        assertEquals(Integer.MIN_VALUE, generator.next());
        try {
            generator = new SerialIntGenerator(-1);
            fail("allowed to wrap");
        } catch (RuntimeException e) {
        }
    }

    private static class TestThread implements Runnable {

        private static int count = 0;

        private final SerialIntGenerator generator;
        private final Set set;
        private final Object lock;

        private TestThread(SerialIntGenerator generator, Set set, Object lock) {
            synchronized (lock) {
                count++;
            }
            this.generator = generator;
            this.set = set;
            this.lock = lock;
            (new Thread(this)).start();
        }

        static int getCount() {
            return count;
        }

        public void run() {
            for (int i = 0; i < 1000; i++) {
                int next = generator.next();
                assertTrue(set.add(new Integer(next)));
            }
            synchronized (lock) {
                count--;
                if (count == 0) {
                    lock.notifyAll();
                }
            }
        }

    }

    public void testReset() {
        SerialIntGenerator generator = new SerialIntGenerator();
        assertEquals(0, generator.next());
        assertEquals(1, generator.next());
        generator.reset();
        assertEquals(0, generator.next());
    }

}
