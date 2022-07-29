/*
 * AsyncInvocationResponseTest
 * 
 * Created 08/15/2006
 */
package com.topcoder.farm.client.invoker;


/**
 * Test case for AsyncInvocationResponse class
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class AsyncInvocationResponseTest { // extends MTTestCase {
//
//    public void testInitialState() throws Exception {
//        AsyncInvocationResponse r = new AsyncInvocationResponse();
//        assertFalse(r.isDone());
//        assertFalse(r.isCancelled());
//    }
//
//    /**
//     * Timeout works properly
//     */
//    public void testGetTimeout() throws Exception {
//        AsyncInvocationResponse r = new AsyncInvocationResponse();
//        try {
//            startTiming();
//            r.get(150);
//            fail("expected TimeoutException");
//        } catch (TimeoutException e) {
//            final long endTiming = endTiming();
//            assertTrue(endTiming >= 140);
//            assertFalse(r.isDone());
//            assertFalse(r.isCancelled());
//        }
//    }
//
//    /**
//     * After cancellation get throws CancellationException
//     */
//    public void testGetWhenCancelled() throws Exception {
//        AsyncInvocationResponse r = new AsyncInvocationResponse();
//        r.cancel();
//        try {
//            r.get(100);
//            fail("expected CancellationException");
//        } catch (CancellationException e) {
//        }
//        try {
//            r.get();
//            fail("expected CancellationException");
//        } catch (CancellationException e) {
//        }
//        assertTrue(r.isDone());
//        assertTrue(r.isCancelled());
//    }
//
//    /**
//     * When result was set get exits with result immediately
//     */
//    public void testGetWhenResultSet() throws Exception {
//        AsyncInvocationResponse r = new AsyncInvocationResponse();
//        InvocationResponse resp = buildResponse();
//        r.setResult(resp);
//        startTiming();
//        InvocationResponse response = r.get(100);
//        assertTrue(endTiming() < 50);
//        assertTrue(response == resp);
//        startTiming();
//        response = r.get(100);
//        assertTrue(endTiming() < 50);
//        assertTrue(response == resp);
//        assertTrue(r.isDone());
//        assertFalse(r.isCancelled());
//    }
//    
//    /**
//     * When an exception was set get throws the farm exception
//     */
//    public void testGetWhenExceptionSet() throws Exception {
//        AsyncInvocationResponse r = new AsyncInvocationResponse();
//        r.setException(new FarmException());
//        try {
//            r.get(100);
//            fail("expected FarmException");
//        } catch (FarmException e) {
//        }
//        try {
//            r.get();
//            fail("expected FarmException");
//        } catch (FarmException e) {
//        }
//        assertTrue(r.isDone());
//        assertFalse(r.isCancelled());
//    }
//
//    /**
//     * If a result was set cancel doesn't succeed
//     */
//    public void testAfterResultNotCancel() throws Exception {
//        AsyncInvocationResponse r = new AsyncInvocationResponse();
//        assertTrue(r.setResult(buildResponse()));
//        assertFalse(r.cancel());
//    }
//
//    /**
//     * If a result was set setException doesn't succeed
//     */
//    public void testAfterResultNotException() throws Exception {
//        AsyncInvocationResponse r = new AsyncInvocationResponse();
//        assertTrue(r.setResult(buildResponse()));
//        assertFalse(r.setException(new FarmException()));
//    }
//
//    /**
//     * If exception was set, cancel doesn't succeed
//     */
//    public void testAfterExceptionNotCancel() throws Exception {
//        AsyncInvocationResponse r = new AsyncInvocationResponse();
//        assertTrue(r.setException(new FarmException()));
//        assertFalse(r.cancel());
//    }
//
//    /**
//     * If exception was set, setResult doesn't succeed
//     */
//    public void testAfterExceptionNotResult() throws Exception {
//        AsyncInvocationResponse r = new AsyncInvocationResponse();
//        assertTrue(r.setException(new FarmException()));
//        assertFalse(r.setResult(buildResponse()));
//    }
//
//    
//    /**
//     * If cancelled, setException doesn't succeed
//     */
//    public void testAfterCancelNotException() throws Exception {
//        AsyncInvocationResponse r = new AsyncInvocationResponse();
//        assertTrue(r.cancel());
//        assertFalse(r.setException(new FarmException()));
//    }
//
//    /**
//     * If cancelled, setResult doesn't succeed
//     */
//    public void testAfterCancelNotResult() throws Exception {
//        AsyncInvocationResponse r = new AsyncInvocationResponse();
//        assertTrue(r.cancel());
//        assertFalse(r.setResult(buildResponse()));
//    }
//
//    private InvocationResponse buildResponse() {
//        return new InvocationResponse("id",null,null);
//    }
//
//    /**
//     * If the thread waiting is interrupted, an InterruptedException is
//     * thrown 
//     */
//    public void testMtGet1AndInterrupt() throws Exception {
//        final AsyncInvocationResponse r = new AsyncInvocationResponse();
//        final Thread thread = Thread.currentThread();
//        
//        run(new Runnable() {
//            public void run() {
//                sleep(100);
//                thread.interrupt();
//            }
//        });
//        startAll();
//        try {
//            try {
//                startTiming();
//                r.get();
//                fail("expected InterruptedException");
//            } catch (InterruptedException e) {
//                assertTrue(endTiming() < 150);
//            }
//        } finally {
//            waitAll(1);
//        }
//    }
//    
//    /**
//     * If the thread waiting is interrupted, an InterruptedException is
//     * thrown 
//     * 
//     * <code>get(long)</code> method 
//     */
//    public void testMtGet2AndInterrupt() throws Exception {
//        final AsyncInvocationResponse r = new AsyncInvocationResponse();
//        final Thread thread = Thread.currentThread();
//        run(new Runnable() {
//            public void run() {
//                sleep(100);
//                thread.interrupt();
//            }
//        });
//        startAll();
//        try {
//            try {
//                startTiming();
//                r.get(500);
//                fail("expected InterruptedException");
//            } catch (InterruptedException e) {
//                assertTrue(endTiming() < 150);
//            }
//        } finally {
//            waitAll(1);
//        }
//    }
//
//    /**
//     * If the AsyncInvocationResponse is cancelled while other thread is waiting
//     * a CancellationException is thrown 
//     */
//    public void testMtGet1AndCancel() throws Exception {
//        final AsyncInvocationResponse r = new AsyncInvocationResponse();
//        run(new Runnable() {
//            public void run() {
//                sleep(100);
//                r.cancel();
//            }
//        });
//        startAll();
//        try {
//            try {
//                startTiming();
//                r.get();
//                fail("expected CancellationException");
//            } catch (CancellationException e) {
//                assertTrue(endTiming() < 150);
//            }
//        } finally {
//            waitAll(1);
//        }
//    }
//    
//    /**
//     * If the AsyncInvocationResponse is cancelled while other thread is waiting
//     * a CancellationException is thrown 
//     */
//    public void testMtGet2AndCancel() throws Exception {
//        final AsyncInvocationResponse r = new AsyncInvocationResponse();
//        run(new Runnable() {
//            public void run() {
//                sleep(100);
//                r.cancel();
//            }
//        });
//        startAll();
//        try {
//            try {
//                startTiming();
//                r.get(500);
//                fail("expected CancellationException");
//            } catch (CancellationException e) {
//                assertTrue(endTiming() < 150);
//            }
//        } finally {
//            waitAll(1);
//        }
//    }
//    
//    /**
//     * If the AsyncInvocationResponse is set an Exception while other thread is waiting
//     * a FarmException is thrown 
//     */
//    public void testMtGet1AndException() throws Exception {
//        final AsyncInvocationResponse r = new AsyncInvocationResponse();
//        run(new Runnable() {
//            public void run() {
//                sleep(100);
//                r.setException(new FarmException());
//            }
//        });
//        startAll();
//        try {
//            try {
//                startTiming();
//                r.get();
//                fail("expected FarmException");
//            } catch (FarmException e) {
//                assertTrue(endTiming() < 150);
//            }
//        } finally {
//            waitAll(1);
//        }
//    }
//    
//    /**
//     * If the AsyncInvocationResponse is set an Exception while other thread is waiting
//     * a FarmException is thrown 
//     */
//    public void testMtGet2AndException() throws Exception {
//        final AsyncInvocationResponse r = new AsyncInvocationResponse();
//        run(new Runnable() {
//            public void run() {
//                sleep(100);
//                r.setException(new FarmException());
//            }
//        });
//        startAll();
//        try {
//            try {
//                startTiming();
//                r.get(500);
//                fail("expected FarmException");
//            } catch (FarmException e) {
//                assertTrue(endTiming() < 150);
//            }
//        } finally {
//            waitAll(1);
//        }
//    }
//    
//    
//    /**
//     * If the AsyncInvocationResponse is set a Result  while other thread is waiting
//     * the result is returned immediately (waking up the sleeping thread).
//     */
//    public void testMtGet1AndResult() throws Exception {
//        final AsyncInvocationResponse r = new AsyncInvocationResponse();
//        run(new Runnable() {
//            public void run() {
//                sleep(100);
//                r.setResult(new InvocationResponse("1", null, null));
//            }
//        });
//        startAll();
//        try {
//            startTiming();
//            r.get();
//            assertTrue(endTiming() < 150);
//        } finally {
//            waitAll(1);
//        }
//    }
//
//    /**
//     * If the AsyncInvocationResponse is set a Result  while other thread is waiting
//     * the result is returned immediately (waking up the sleeping thread).
//     */
//    public void testMtGet2AndResult() throws Exception {
//        final AsyncInvocationResponse r = new AsyncInvocationResponse();
//        run(new Runnable() {
//            public void run() {
//                sleep(100);
//                r.setResult(new InvocationResponse("1", null, null));
//            }
//        });
//        startAll();
//        try {
//            startTiming();
//            r.get(500);
//            assertTrue(endTiming() < 150);
//        } finally {
//            waitAll(1);
//        }
//    }
}

