/*
 * ThreadPoolRunnerTest
 *
 * Created 07/24/2006
 */
package com.topcoder.farm.shared.util.concurrent.runner;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import com.topcoder.farm.shared.util.concurrent.runner.ThreadPoolRunner;

/**
 * Test case for the SingleThreadRunner calss
 *
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ThreadPoolRunnerTest extends TestCase {
    private static final int POOL_SIZE = 4;
    private ThreadPoolRunner runner;
    private Set numbers;
    private Set expected;

    protected void setUp() throws Exception {
        runner = new ThreadPoolRunner("X", POOL_SIZE);
        numbers = Collections.synchronizedSet(new HashSet());
        expected = Collections.synchronizedSet(new HashSet());
    }

    protected void tearDown() throws Exception {
        runner.stop(true);
    }

    /**
     * If one task is added the task is run and when stop is used
     * the bg thread is stopped
     */
    public void testRunOne() throws Exception {
        addTask(1);
        Thread.sleep(100);
        assertEquals(numbers, expected);
        int thPoolCnt = runner.getPoolSize();
        int thCnt = Thread.activeCount();
        runner.stop(false);
        Thread.sleep(100);
        assertEquals(thCnt-thPoolCnt, Thread.activeCount());
    }


    /**
     * If many tasks are added, all tasks are run in the proper order and
     * when stop is used the bg thread is stopped
     */
    public void testRunMany() throws Exception {
        for (int i = 0; i < 30; i++) {
            addTask(i);
        }
        Thread.sleep(200);
        assertEquals(numbers, expected);
        int thCnt = Thread.activeCount();
        int thPoolCnt = runner.getPoolSize();
        runner.stop(false);
        Thread.sleep(300);
        assertEquals(thCnt-thPoolCnt, Thread.activeCount());
        assertEquals(thPoolCnt, POOL_SIZE);
    }
    
    
    /**
     * If many tasks are added, all tasks are run in the proper order and
     * when stop is used the bg thread is stopped
     */
    public void testRunManyWithMinThreads() throws Exception {
        runner = new ThreadPoolRunner("X", POOL_SIZE, 2*POOL_SIZE, 2000);
        for (int i = 0; i < 30; i++) {
            addTask(i);
        }
        Thread.sleep(200);
        assertEquals(numbers, expected);
        int thCnt = Thread.activeCount();
        int thPoolCnt = runner.getPoolSize();
        assertEquals(2*POOL_SIZE, thPoolCnt);
        Thread.sleep(2000);
        int thPoolCnt2 = runner.getPoolSize();
        runner.stop(false);
        Thread.sleep(300);
        assertEquals(thCnt-thPoolCnt, Thread.activeCount());
        assertEquals(thPoolCnt2, POOL_SIZE);
    }

    /**
     * If one task throws an exception the next task are run correctly
     */
    public void testRunExceptionHandled() throws Exception {
        runner.run(new Runnable() {
            public void run() {
                throw new IllegalStateException("");
            }
        });
        addTask(1);
        Thread.sleep(200);
        assertEquals(numbers, expected);
        int thCnt = Thread.activeCount();
        int thPoolCnt = runner.getPoolSize();
        runner.stop(false);
        Thread.sleep(100);
        assertEquals(thCnt-thPoolCnt, Thread.activeCount());
    }


    /**
     * If stop(true) is used the running task is interrupted
     */
    public void testTaskInterrupted() throws Exception {
        runner.run(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        });
        addTask(1);
        Thread.sleep(100);
        int thCnt = Thread.activeCount();
        int thPoolCnt = runner.getPoolSize();
        runner.stop(true);
        Thread.sleep(100);
        assertEquals(thCnt-thPoolCnt, Thread.activeCount());
        assertEquals(numbers, expected);
    }

    /**
     * If stopAccepting is used, no new task can be added and
     * after running the last one the BG thread is stopped
     * @throws Exception
     */
    public void testStopAccepting() throws Exception {
        for (int i = 0; i < 30; i++) {
            addTask(i);
        }
        Thread.sleep(500);
        int thCnt = Thread.activeCount();
        int thPoolCnt = runner.getPoolSize();
        runner.stopAccepting();
        try {
            addTask(31);
            fail("Expected exception");
        } catch (Exception e) {
        }
        expected.remove(new Integer(31));
        assertEquals(numbers, expected);
        Thread.sleep(100);
        assertEquals(thCnt-thPoolCnt, Thread.activeCount());
    }


    /**
     * If awaitAllTaskTermination is used, all task must be finished
     * before the calling thread is unblocked
     * @throws Exception
     */
    public void testAwait() throws Exception {
        for (int i = 0; i < 30; i++) {
            addTask(i);
        }
        runner.stopAccepting();
        runner.awaitAllTaskTermination();
        assertEquals(30, numbers.size());

    }
    /**
     * Add a task and the expected result
     */
    private void addTask(int i) {
        expected.add(new Integer(i));
        runner.run(new TestRun(new Integer(i)));
    }



    /**
     * Runnable for test case
     */
    public class TestRun implements Runnable {
        Integer value;

        public TestRun(Integer value) {
            this.value = value;
        }

        public void run() {
            System.out.println(Thread.currentThread().getName());
            numbers.add(value);
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }
        }
    }
}
