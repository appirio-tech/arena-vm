/*
 * ManagedFutureImplTest
 * 
 * Created Nov 2, 2007
 */
package com.topcoder.shared.util.concurrent;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

/**
 * Managed Future Test cases 
 *  
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ManagedFutureImplTest extends TestCase {
    private static final String RESULT = "result";
    private static final String TEXT = "Exception Text";
    private List releases;
    

    protected void setUp() throws Exception {
        releases = Collections.synchronizedList(new LinkedList());
    }
    
    public void testSetValueGet() throws Exception {
        final ManagedFutureImpl future = createFuture();
        future.setValue(RESULT);
        assertEquals(RESULT, future.get(1, TimeUnit.SECONDS));
        assertEquals(RESULT, future.get());
        checkReleases();
    }
    
    public void testSetExceptionGet() throws Exception {
        final ManagedFutureImpl future = createFuture();
        future.setException(new Exception(TEXT));
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
                future.setValue(RESULT);
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
                future.setException(new Exception(TEXT));
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
                future.cancel(true);
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
                future.setValue(TEXT);
                future.cancel(true);
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
        waitThread(50);
        assertEquals("1", releases.get(0));
    }

    private ManagedFutureImpl createFuture() {
        final ManagedFutureImpl future = new ManagedFutureImpl("1", new ManagedFutureImpl.FutureHandler<String>() {
            public void futureReady(String id) {
                releases.add(id);
            }
            public boolean cancel(String id, boolean mayInterrupt) {
                return false;
            }
        });
        return future;
    }
    
    private void waitThread(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
