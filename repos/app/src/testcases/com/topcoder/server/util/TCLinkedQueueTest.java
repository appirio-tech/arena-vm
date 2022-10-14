package com.topcoder.server.util;

import junit.framework.TestCase;

public final class TCLinkedQueueTest extends TestCase {

    private TCLinkedQueue queue;

    public TCLinkedQueueTest(String name) {
        super(name);
    }

    protected void setUp() {
        queue = new TCLinkedQueue();
    }

    public void testPutTake() {
        String string = "string";
        queue.put(string);
        try {
            String string2 = (String) queue.take();
            assertEquals(string, string2);
        } catch (InterruptedException e) {
            fail();
        }
    }

    private static class Task {

        private final Thread thread;

        private Task(Runnable runnable) {
            thread = new Thread(runnable);
            thread.start();
        }

        private boolean isAlive() {
            return thread.isAlive();
        }

    }

    public void testPollZero() {
        Task task = new Task(new Runnable() {
            public void run() {
                try {
                    queue.poll(0);
                } catch (InterruptedException e) {
                }
            }
        });
        try {
            Thread.sleep(25);
        } catch (InterruptedException e) {
            fail();
        }
        assertTrue(!task.isAlive());
    }

}
