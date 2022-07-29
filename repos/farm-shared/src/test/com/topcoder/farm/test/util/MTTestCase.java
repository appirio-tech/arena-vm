/*
 * MTTestCase
 * 
 * Created 07/15/2006
 */
package com.topcoder.farm.test.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

/**
 * Base class for helping with multithread test cases
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class MTTestCase extends TestCase {
    /**
     * List containing all threads schedulled for execution
     */
    private List threads;
    /**
     * Time to wait in milliseconds between each thread start
     */
    private int waitBetweenStarts = 5;

    /**
     * Contains the currentTimeInMillis at the moment the startTiming was called
     */
    private long initTs;
    
    /**
     * Contains the currentTimeInMillis at the moment the endTiming was called
     */
    private long endTs;
    
    
    public MTTestCase() {
    }

    public MTTestCase(String arg0) {
        super(arg0);
    }

    protected void setUp() throws Exception {
        threads = new ArrayList();
    }
    
    protected void tearDown() throws Exception {
        threads.clear();
    }

    
    /**
     * Starts all threads added using the <code>run</code> method
     * The threads are started in the order in which they were added.
     * If <code>waitBetweenStarts</code> is greater than 0, each thread
     * will be start with a gap of <code>waitBetweenStarts</code> ms.
     *  
     * @throws InterruptedException
     */
    protected void startAll() throws InterruptedException {
        for (Iterator it = threads.iterator(); it.hasNext();) {
            InnerThread element = (InnerThread) it.next();
            synchronized (element) {
                element.go();
            }
            if (waitBetweenStarts > 0) Thread.sleep(waitBetweenStarts);
        }
    }

    /**
     * Starts all threads and wait for completition of all of them
     * @throws InterruptedException
     */
    protected void startAllAndWait() throws InterruptedException {
        startAll();
        waitAll();
    }

    /**
     * Waits for all started threads added using the <code>run</code> to complete.
     * @throws InterruptedException
     */
    protected void waitAll() {
        for (Iterator it = threads.iterator(); it.hasNext();) {
            Thread element = (Thread) it.next();
            try {
                element.join();
            } catch (Exception e) {
            }
        }
    }

    
    /**
     * Waits for all started threads added using the <code>run</code> to complete.
     * 
     * @return false if one thread had to be interrupted
     * @throws InterruptedException
     */
    protected boolean waitAll(long time) {
        boolean result = true; 
        for (Iterator it = threads.iterator(); it.hasNext();) {
            Thread element = (Thread) it.next();
            try {
                element.join(time);
            } catch (Exception e) {
            }
            if (element.isAlive()) {
                element.interrupt();
                result = false;
            }
        }
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
        }
        return result;
    }
    
    /**
     * Schedules a runnable to for execution 
     */
    protected void run(final Runnable runnable) {
        Thread thread = new InnerThread(runnable);
        threads.add(thread);
        thread.start();
    }
    
    /**
     * @return The waitBetweenStarts in ms
     */
    public int getWaitBetweenStarts() {
        return waitBetweenStarts;
    }

    /**
     * Set the waitBetweenStarts in milliseconds
     * @param waitBetweenStarts Time to wait in between each thread start 
     */
    public void setWaitBetweenStarts(int waitBetweenStarts) {
        this.waitBetweenStarts = waitBetweenStarts;
    }

    /**
     * Set the initial timestamps
     */
    public void startTiming() {
        this.initTs = System.currentTimeMillis();
    }

    /**
     * Sets the ending timestamp and returns the 
     * @return ending timestamp - start timestamp
     */
    public long endTiming() {
        this.endTs = System.currentTimeMillis();
        return endTs - initTs;
    }
    
    /**
     * @return Current timestamp  - initial Timestamp
     */
    public long lapTiming() {
        return System.currentTimeMillis() - initTs;
    }
    
    /**
     * @return ending timestamp - start timestamp
     */
    public long getTiming() {
        return endTs - initTs;
    }

    /**
     * Sleeps the calling thread. Doesn't throw {@link InterruptedException}
     * @param ms time to sleep
     * 
     * @return true if the thread was interrupted.
     */
    public boolean sleep(long ms) {
        try {
            Thread.sleep(ms);
            return false;
        } catch (InterruptedException e) {
            return true;
        }
    }
    
    /**
     * Thread used to run runnable actions
     */
    private static final class InnerThread extends Thread {
        private boolean mustStart = false;
        private final Runnable runnable;
        private final Object mutex = new Object();

        private InnerThread(Runnable runnable) {
            this.runnable = runnable;
        }
        
        public void run() {
            synchronized (mutex) {
                try {
                    if (!mustStart) mutex.wait();
                } catch (InterruptedException e) {
                }
            }
            System.out.println(System.currentTimeMillis()+" - Init Thread: " + Thread.currentThread().getName());
            runnable.run();
            System.out.println(System.currentTimeMillis()+" - End  Thread: " + Thread.currentThread().getName());
        }
        
        public void go() {
            synchronized (mutex) {
                mustStart = true;
                mutex.notify();
            }
        }
    }    
    
    public void testMTTestCase() throws Exception {

    }
}
