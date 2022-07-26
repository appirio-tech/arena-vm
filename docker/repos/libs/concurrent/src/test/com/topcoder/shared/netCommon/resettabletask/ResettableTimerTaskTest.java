/*
 * ResettableTimerTaskTest
 * 
 * Created 03/28/2006
 */
package com.topcoder.shared.netCommon.resettabletask;

import com.topcoder.shared.netCommon.resettabletask.ResettableTimerTask;

import junit.framework.TestCase;

/**
 * Test case for ResettableTimerTask class
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ResettableTimerTaskTest extends TestCase {
    
    /**
     * Field used for testing number of times the doAction method is invoked
     */
    private int field;
    
    /**
     * Task used for testing 
     */
    private ResettableTimerTask task;
    
    /**
     * Thread running the task
     */
    private Thread thread;
    
    /**
     * Tests that invoking stop ends the Tasks.
     */
    public void testStop() throws Exception {
        Thread.sleep(250);
        task.stop();
        Thread.sleep(250);
        assertEquals(field, 2);
    }
    
    /**
     * Tests that invoking reset countdown starts again
     */
    public void testReset() throws Exception {
        Thread.sleep(50);
        task.reset();
        Thread.sleep(170);
        task.stop();
        Thread.sleep(250);
        assertEquals(field, 1);
    }
    
    /**
     * Tests that if doAction returns true, task will end
     */
    public void testDoActionStops() throws Exception {
        Thread.sleep(700);
        task.stop();
        Thread.sleep(100);
        assertEquals(field, 5);
    }
    
    
    protected void setUp() throws Exception {
        field = 0;
        task = new ResettableTimerTask(100) {
            protected boolean doAction() {
                field++;
                return field == 5;
            }
        };
        thread = new Thread(task);
        thread.start();
    }
    
    
    protected void tearDown() throws Exception {
        task.stop();
        thread.join(100);
        if (thread.isAlive()) {
            thread.interrupt();
        }
    }
}
