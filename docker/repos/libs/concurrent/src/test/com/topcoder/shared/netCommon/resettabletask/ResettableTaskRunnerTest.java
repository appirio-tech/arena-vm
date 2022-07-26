/*
 * ResettableTaskRunnerTest
 * 
 * Created 03/28/2006
 */
package com.topcoder.shared.netCommon.resettabletask;

import com.topcoder.shared.netCommon.resettabletask.ResettableTaskRunner;
import com.topcoder.shared.netCommon.resettabletask.ResettableTimerTask;

import junit.framework.TestCase;

/**
 * Test case for ResettableTaskRunner class
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ResettableTaskRunnerTest extends TestCase {
    private int value1 = 0;
    private int value2 = 0;
   
    /**
     * Test normal flow.
     * Both tasks must be started and runned 2 times
     */
    public void testStartAll() throws Exception {
        ResettableTaskRunner runner = new ResettableTaskRunner();
        runner.registerTask("TEST1", buildTask1());
        runner.registerTask("TEST2", buildTask2());
        runner.start();
        Thread.sleep(250);
        runner.stop();
        assertEquals(2, value1);
        assertEquals(2, value2);
    }

    /**
     * Tests that all tasks are stopped when stop is invoked
     */
    public void testStopAll() throws Exception {
        ResettableTaskRunner runner = new ResettableTaskRunner();
        runner.registerTask("TEST1", buildTask1());
        runner.registerTask("TEST2", buildTask2());
        runner.start();
        Thread.sleep(10);
        runner.stop();
        Thread.sleep(200);
        assertEquals(0, value1);
        assertEquals(0, value2);
    }

    /**
     * Tests that invoking start two consecutive times 
     * throws IllegalStateException
     */
    public void testDualStart() throws Exception {
        ResettableTaskRunner runner = new ResettableTaskRunner();
        runner.registerTask("TEST1", buildTask1());
        runner.registerTask("TEST2", buildTask2());
        runner.start();
        try {
            runner.start();
            fail("Expected IllegalStateException");
        } catch (IllegalStateException e) {
            //Success
        } finally {
            runner.stop();
        }
    }
    
    /**
     * Tests that invoking stop two consecutive times 
     * throws IllegalStateException
     */
    public void testDualStop() throws Exception {
        ResettableTaskRunner runner = new ResettableTaskRunner();
        runner.registerTask("TEST1", buildTask1());
        runner.registerTask("TEST2", buildTask2());
        runner.start();
        runner.stop();
        try {
            runner.stop();
            fail("Expected IllegalStateException");
        } catch (IllegalStateException e) {
            //Success
        }
    }
    
    /**
    * Tests that invoking stop before start 
    * throws IllegalStateException
    */
    public void testStopBeforeStart() throws Exception {
        ResettableTaskRunner runner = new ResettableTaskRunner();
        runner.registerTask("TEST1", buildTask1());
        runner.registerTask("TEST2", buildTask2());
        try {
            runner.stop();
            fail("Expected IllegalStateException");
        } catch (IllegalStateException e) {
            //Success
        }
    }
    
    /**
     * Tests that invoking register on a started ResettableTaskRunner
     * throws IllegalStateException
     */
    public void testRegisterOnStarted() throws Exception {
        ResettableTaskRunner runner = new ResettableTaskRunner();
        runner.registerTask("TEST1", buildTask1());
        runner.start();
        try {
            runner.registerTask("TEST2", buildTask2());
            fail("Expected IllegalStateException");
        } catch (IllegalStateException e) {
            //Success
        } finally {
            runner.stop();
        }
    }
    
    private ResettableTimerTask buildTask1() {
        return new ResettableTimerTask(100){
            protected boolean doAction() {
                value1++;
                return false;
            }
        };
    }

    private ResettableTimerTask buildTask2() {
        return new ResettableTimerTask(100){
            protected boolean doAction() {
                value2++;
                return false;
            }
        };
    }
}
