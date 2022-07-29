package com.topcoder.shared.util;

import junit.framework.TestCase;

import com.topcoder.shared.util.StoppableThread;

public final class StoppableThreadTest extends TestCase {

    public StoppableThreadTest(String name) {
        super(name);
    }

    public void testRunning() {
        Client c = new Client();
        assertEquals(0, getDiff(c));
        c.start();
        assertTrue("has the thread started?", getDiff(c) > 0);
        assertTrue("is the thread running?", getDiff(c) > 0);
        c.stop();
        assertEquals("is the thread still running?", 0, getDiff(c));
    }

    public void testStopped() {
        Client c = new Client();
        assertEquals(true, c.isStopped());
        c.start();
        assertEquals(false, c.isStopped());
        c.stop();
        assertEquals(true, c.isStopped());
    }

    public void testIllegalState() {
        Client c = new Client();
        c.stop();
        c.start();
        try {
            c.start();
            fail("allowed to start twice");
        } catch (IllegalStateException e) {
        }
        c.stop();
        c.stop();
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            fail();
        }
    }

    private long getDiff(Client c) {
        long k = c.getCount();
        sleep(300);
        long diff = c.getCount() - k;
        return diff;
    }

    private static class Client implements StoppableThread.Client {

        private long count;
        private StoppableThread thread = new StoppableThread(this, "Client");

        private long getCount() {
            return count;
        }

        private boolean isStopped() {
            return thread.isStopped();
        }

        private void start() {
            thread.start();
        }

        private void stop() {
            try {
                thread.stopThread();
            } catch (InterruptedException e) {
                fail();
            }
        }

        public void cycle() {
            count++;
        }
    }

    public void testFastStop() {
        SlowClient client = new SlowClient();
        client.start();
        sleep(20);
        client.stop();
        long time = 1000;
        long start = System.currentTimeMillis();
        try {
            client.join(time);
        } catch (InterruptedException e) {
            fail();
        }
        long elapsed = System.currentTimeMillis() - start;
        long expected = 1;
        assertTrue("elapsed=" + elapsed + ", expected=" + expected, elapsed <= expected);
        assertEquals(true, client.isStopped());
    }

    private static class SlowClient implements StoppableThread.Client {

        private StoppableThread thread = new StoppableThread(this, "SlowClient");

        private boolean isStopped() {
            return thread.isStopped();
        }

        private void start() {
            thread.start();
        }

        private void stop() {
            try {
                thread.stopThread();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void join(long millis) throws InterruptedException {
            thread.join(millis);
        }

        public void cycle() throws InterruptedException {
            Thread.sleep(100000);
        }
    }

    public void testStopItself() {
        StopClient client = new StopClient();
        client.start();
        try {
            client.join();
        } catch (InterruptedException e) {
            fail();
        }
        assertTrue(client.isGood);
    }

    private static class StopClient implements StoppableThread.Client {

        private long count;
        private StoppableThread thread = new StoppableThread(this, "Client");
        private boolean isGood = true;

        private void start() {
            thread.start();
        }

        private void join() throws InterruptedException {
            thread.join();
        }

        public void cycle() {
            count++;
            if (count == 5) {
                try {
                    thread.stopThread();
                } catch (InterruptedException e) {
                    isGood = false;
                    fail();
                }
            }
        }
    }

}
