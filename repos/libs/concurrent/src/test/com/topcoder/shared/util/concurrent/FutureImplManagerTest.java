/*
 * FutureImplManagerTest
 * 
 * Created Nov 5, 2007
 */
package com.topcoder.shared.util.concurrent;

import java.util.Collections;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class FutureImplManagerTest extends TestCase {
    private static final String ID = "1";
    private static final String RESULT = "result";
    private static final String TEXT = "Exception Text";
    private FutureImplManager<String, String> futureManager;
    

    protected void setUp() throws Exception {
        futureManager = new FutureImplManager<String, String>();
    }
    
    public void testSetValueGet() throws Exception {
        final ManagedFutureImpl future = createFuture();
        assertEquals(1, futureManager.getPendingFuturesSize());
        assertEquals(Collections.singleton(ID), futureManager.getPendingFutureIds());
        futureManager.setValue(ID, RESULT);
        assertEquals(RESULT, future.get(1, TimeUnit.SECONDS));
        assertEquals(RESULT, future.get());
        assertEquals(0, futureManager.getPendingFuturesSize());
        checkReleases();
    }
    
    public void testSetExceptionGet() throws Exception {
        final ManagedFutureImpl future = createFuture();
        futureManager.setException(ID, new Exception(TEXT));
        try {
            future.get(1, TimeUnit.SECONDS);
            fail("Expecting exception");
        } catch (ExecutionException e) {
            if (!e.getCause().getMessage().equals(TEXT)) {
                fail("Exception message does not match");
            }
            checkReleases();
        }
    }
    
    
    
    public void testGetWaitAndSetValue() throws Exception {
        final ManagedFutureImpl future = createFuture();
        Thread thread = new Thread() {
            public void run() {
                waitThread(300);
                futureManager.setValue(ID, RESULT);
            }
        };
        thread.start();
        long st = System.currentTimeMillis();
        assertEquals(RESULT, future.get(400, TimeUnit.SECONDS));
        long finalSt = System.currentTimeMillis();
        assertEquals(RESULT, future.get());
        checkReleases();
        assertTrue( (finalSt - st) > 290);
    }
    
    public void testGetWaitAndSetException() throws Exception {
        final ManagedFutureImpl future = createFuture();
        Thread thread = new Thread() {
            public void run() {
                waitThread(300);
                futureManager.setException(ID, new Exception(TEXT));
            }
        };
        thread.start();
        long st = System.currentTimeMillis();
        try {
            future.get(400, TimeUnit.SECONDS);
            fail("Expecting exception");
        } catch (ExecutionException e) {
            long finalSt = System.currentTimeMillis();
            assertTrue( (finalSt - st) > 290);
            if (!e.getCause().getMessage().equals(TEXT)) {
                fail("Exception message does not match");
            }
        }
        checkReleases();
    }
    
    public void testCancel() throws Exception {
        final ManagedFutureImpl future = createFuture();
        Thread thread = new Thread() {
            public void run() {
                waitThread(300);
                futureManager.cancel(ID, true);
            }
        };
        thread.start();
        long st = System.currentTimeMillis();
        try {
            future.get(400, TimeUnit.SECONDS);
            fail("Expecting exception");
        } catch (CancellationException e) {
            long finalSt = System.currentTimeMillis();
            assertTrue( (finalSt - st) > 290);
        } 
        checkReleases();
    }
    
    public void testCancelAfterSetValue() throws Exception {
        final ManagedFutureImpl future = createFuture();
        Thread thread = new Thread() {
            public void run() {
                waitThread(300);
                futureManager.setValue(ID, TEXT);
                futureManager.cancel(ID, true);
            }
        };
        thread.start();
        long st = System.currentTimeMillis();
        try {
            future.get(400, TimeUnit.SECONDS);
            Thread.sleep(100);
            future.get(400, TimeUnit.SECONDS);
            long finalSt = System.currentTimeMillis();
            assertTrue( (finalSt - st) > 290);
        } catch (CancellationException e) {
            fail("Unexpected exception");
        } 
        checkReleases();
    }

    private void checkReleases() {
        //waitThread(50);
    }

    private ManagedFutureImpl createFuture() {
        return futureManager.newFuture(ID);
    }
    
    private void waitThread(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
