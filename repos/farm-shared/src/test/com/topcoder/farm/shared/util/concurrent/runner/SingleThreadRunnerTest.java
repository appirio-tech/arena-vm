/*
 * SingleThreadRunnerTest
 * 
 * Created 07/24/2006
 */
package com.topcoder.farm.shared.util.concurrent.runner;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.topcoder.farm.shared.util.concurrent.runner.Runner;
import com.topcoder.farm.shared.util.concurrent.runner.SingleThreadRunner;

/**
 * Test case for the SingleThreadRunner calss
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class SingleThreadRunnerTest extends TestCase {
    private Runner runner;
    private List numbers;
    private List expected;

    protected void setUp() throws Exception {
        runner = new SingleThreadRunner();
        numbers = new ArrayList();
        expected = new ArrayList();
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
        int thCnt = Thread.activeCount();
        runner.stop(false);
        Thread.sleep(100);
        assertEquals(thCnt-1, Thread.activeCount());
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
        runner.stop(false);
        Thread.sleep(100);
        assertEquals(thCnt-1, Thread.activeCount());
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
        runner.stop(false);
        Thread.sleep(100);
        assertEquals(thCnt-1, Thread.activeCount());
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
        runner.stop(true);
        Thread.sleep(100);
        assertEquals(thCnt-1, Thread.activeCount());
        assertEquals(numbers, new ArrayList());
        
    }
    
    /**
     * If stopAccepting is used, no new task can be added and
     * after running the last one the BG thread is stopped
     * @throws Exception
     */
    public void testStopAccepting() throws Exception {
        addTask(1);
        Thread.sleep(100);
        int thCnt = Thread.activeCount();
        runner.stopAccepting();
        try {
            addTask(2);
            fail("Expected exception");
        } catch (Exception e) {
        }
        expected.remove(1);
        assertEquals(numbers, expected);
        Thread.sleep(100);
        assertEquals(thCnt-1, Thread.activeCount());
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
            numbers.add(value);
        }
    } 
}
