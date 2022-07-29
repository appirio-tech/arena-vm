/*
 * WaiterTest
 * 
 * Created Nov 19, 2007
 */
package com.topcoder.shared.util.concurrent;

import junit.framework.TestCase;


/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class WaiterTest extends TestCase {
    
    public void testWait0() throws Exception {
        final Object mutex = new Object();
        final long[] tsHolder = new long[1];
        final Waiter w = new Waiter(mutex);
        Thread t = new Thread() {
            public void run() {
                    synchronized (mutex) {
                    try {
                        while (!w.elapsed()) {
                            w.await();
                        }
                        fail("Should exit on interrupt");
                    } catch (InterruptedException e) {
                    }
                    tsHolder[0] = System.currentTimeMillis();
                }
            }
        };
        t.start();
        Thread.sleep(500);
        t.interrupt();
        Thread.sleep(100);
        assertTrue(Math.abs(tsHolder[0] - System.currentTimeMillis()) < 120);
        assertFalse(w.elapsed());
    }
    
    
    public void testWaitWithTime() throws Exception {
        final Object mutex = new Object();
        final long[] tsHolder = new long[2];
        final Waiter[] ws = new Waiter[1];
        Thread t = new Thread() {
            public void run() {
                synchronized (mutex) {
                    Waiter w = new Waiter(200, mutex);
                    ws[0] = w;
                    tsHolder[0] = System.currentTimeMillis();
                    try {
                        while (!w.elapsed()) {
                            w.await();
                        }
                    } catch (InterruptedException e) {
                        fail("Unexpected interrupt");
                    }
                    tsHolder[1] = System.currentTimeMillis();
                }
            }
        };
        t.start();
        Thread.sleep(400);
        t.interrupt();
        assertTrue(""+Math.abs(tsHolder[1] - tsHolder[0]), Math.abs(tsHolder[1] - tsHolder[0]) > 190);
        assertTrue(ws[0].elapsed());
     }
    
    
    public void testWait0WithNotifies() throws Exception {
        final Object mutex = new Object();
        final long[] countHolder = new long[1];
        final Waiter w = new Waiter(mutex);
        Thread t = new Thread() {
            public void run() {
                synchronized (mutex) {
                    try {
                        while (!w.elapsed()) {
                            w.await();
                            countHolder[0]++;
                        }
                        fail("Should exit on interrupt");
                    } catch (InterruptedException e) {
                    }
                }
            }
        };
        t.start();
        Thread.sleep(100);
        synchronized (mutex) {
            mutex.notify();
        }
        Thread.sleep(50);
        w.synchNotifyAll();
        Thread.sleep(50);
        synchronized (mutex) {
            mutex.notify();
        }
        Thread.sleep(50);
        w.synchNotifyAll();
        Thread.sleep(50);
        t.interrupt();
        assertEquals(4, countHolder[0]);
    }
    
    public void testWaitOnTimeAndNotifies() throws Exception {
        final Object mutex = new Object();
        final long[] countHolder = new long[1];
        final Waiter[] ws = new Waiter[1];
        final Object o = new Object();
        Thread t = new Thread() {
            public void run() {
                synchronized (o) {
                    o.notify();
                }
                synchronized (mutex) {
                    Waiter w = new Waiter(200, mutex);
                    ws[0] = w;
                    try {
                        while (!w.elapsed()) {
                            w.await();
                            countHolder[0]++;
                        }
                    } catch (InterruptedException e) {
                        fail("Should exit due timeout");
                    }
                }
            }
        };
        synchronized (o) {
            t.start();
            o.wait();
        }
        Thread.sleep(100);
        synchronized (mutex) {
            mutex.notify();
        }
        Thread.sleep(40);
        ws[0].synchNotifyAll();
        Thread.sleep(40);
        synchronized (mutex) {
            mutex.notify();
        }
        Thread.sleep(200);
        ws[0].synchNotifyAll();
        Thread.sleep(100);
        t.interrupt();
        assertEquals(4, countHolder[0]);
    }
    
    
    public void testReset() throws Exception {
        final Object mutex = new Object();
        final long[] tsHolder = new long[2];
        final Waiter[] ws = new Waiter[1];
        final Object o = new Object();
        Thread t = new Thread() {
            public void run() {
                synchronized (o) {
                    o.notify();
                }
                synchronized (mutex) {
                    Waiter w = new Waiter(200, mutex);
                    ws[0] = w;
                    tsHolder[0] = System.currentTimeMillis();
                    try {
                        while (!w.elapsed()) {
                            w.await();
                        }
                    } catch (InterruptedException e) {
                        fail("Unexpected interrupt");
                    }
                    tsHolder[1] = System.currentTimeMillis();
                }
            }
        };
        synchronized (o) {
            t.start();
            o.wait();
        }
        Thread.sleep(190);
        ws[0].reset();
        Thread.sleep(250);
        t.interrupt();
        assertTrue(""+Math.abs(tsHolder[1] - tsHolder[0]), Math.abs(tsHolder[1] - tsHolder[0]) > 350);
        assertTrue(ws[0].elapsed());
     }
}
