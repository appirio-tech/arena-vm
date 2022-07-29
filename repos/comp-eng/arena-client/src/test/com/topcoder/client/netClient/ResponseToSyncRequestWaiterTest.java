/*
 * ResponseToSyncRequestWaiterTest
 * 
 * Created 03/24/2006
 */
package com.topcoder.client.netClient;

import junit.framework.TestCase;


/**
 * Test case for ResponseToSyncRequestWaiter class
 * 
 * @author Diego Belfer (mural)
 * @version $Id: ResponseToSyncRequestWaiterTest.java 71134 2008-06-10 04:53:33Z dbelfer $
 */
public class ResponseToSyncRequestWaiterTest extends TestCase {
    /**
     * Value used as default start timeout in ResponseToSyncRequestWaiter
     * for this tests
     */
    private static final long START_TIMEOUT = 200;
    /**
     * Value used as default end timeout in ResponseToSyncRequestWaiter
     * for this tests
     */
    private static final long END_TIMEOUT = 300;
    
    /**
     * Value used as default inactivity timeout in ResponseToSyncRequestWaiter
     * for this tests
     */
    private static final long INACTIVITY_TIMEOUT = 120;
    
    /**
     * Represents the acceptable difference between the expected elapsed-time and 
     * the real elapsed-time. Could be greater or smaller. machine speed dependent  
     */
    private static final long DELTA = 30;
    
    /**
     * ResponseToSyncRequestWaiter used in almost all tests
     */
    private ResponseToSyncRequestWaiter waiter;
    
    /**
     * Used for timing purposes 
     */
    private long tsStart;
    
    /**
     * Tests that system properties defined are used as timeout values 
     */
    public void testSystemProperties() {
        ResponseToSyncRequestWaiter localWaiter = new ResponseToSyncRequestWaiter();
        assertEquals(START_TIMEOUT, localWaiter.getStartTimeout());
        assertEquals(END_TIMEOUT, localWaiter.getEndTimeout());
        assertEquals(INACTIVITY_TIMEOUT, localWaiter.getInactivityTimeout());
    }
    
    /**
     * Tests that arguments used in constructor call are used as timeout values 
     */
    public void testConstructor() {
        ResponseToSyncRequestWaiter localWaiter = new ResponseToSyncRequestWaiter(5,7,8);
        assertEquals(5, localWaiter.getStartTimeout());
        assertEquals(7, localWaiter.getEndTimeout());
        assertEquals(8, localWaiter.getInactivityTimeout());
    }
    
    /**
     * Tests that if no system properties were defined, and the default constructor is used
     * timeout values are those defined in  ContestConstants.RESPONSE_START_TIMEOUT_MILLIS and
     * ContestConstants.RESPONSE_END_TIMEOUT_MILLIS
     */
    public void testDefaults() {
        System.getProperties().remove("com.topcoder.response.start.timeout");
        System.getProperties().remove("com.topcoder.response.end.timeout");
        System.getProperties().remove("com.topcoder.response.inactivity.timeout");
        ResponseToSyncRequestWaiter localWaiter = new ResponseToSyncRequestWaiter();
        assertEquals(ResponseToSyncRequestWaiter.RESPONSE_START_TIMEOUT_MILLIS, localWaiter.getStartTimeout());
        assertEquals(ResponseToSyncRequestWaiter.RESPONSE_END_TIMEOUT_MILLIS, localWaiter.getEndTimeout());
        assertEquals(ResponseToSyncRequestWaiter.RESPONSE_INACTIVITY_TIMEOUT_MILLIS, localWaiter.getInactivityTimeout());
    }
    
    /**
     * Tests that if we wait only for start, just startTimeOut milliseconds has elapsed
     */
    public void testStartWaits() throws Exception {
        startElapsedTime();
        waiter.blockUntilStart();
        verifyElapse(START_TIMEOUT);
    }

    /**
     * Tests that if we wait for end, startTimeout milliseconds has elapse 
     */
    public void testEndWaits() throws Exception {
        startElapsedTime();
        waiter.blockUntilEnd();
        verifyElapse(START_TIMEOUT);
    }
    
    /**
     * Tests that if we block method has the same behavior than blockUntilEnd 
     */
    public void testBlocksUseEnd() throws Exception {
        startElapsedTime();
        waiter.block();
        verifyElapse(START_TIMEOUT);
    }

    /**
     * Tests that blockUntilStart exits timely if a start-response notification is received
     * by the ResponseToSyncRequestWaiter
     */
    public void testWakeOnStartWaitingForStart() throws Exception {
        notifyStart(100);
        startElapsedTime();
        waiter.blockUntilStart();
        verifyElapse(100);
    }
    
    /**
     * Tests that blockUntilEnd cancels start waiting but continues waiting
     * for the end of the response when a start-response notification is received
     */
    public void testWakeOnStartWaitingForEnd() throws Exception {
        notifyStart(100);
        notifyDataRead(150);
        notifyDataRead(250);
        notifyDataRead(350);
        startElapsedTime();
        waiter.blockUntilEnd();
        verifyElapse(100+END_TIMEOUT);
    }
    
    
    /**
     * Tests that blockUntilStart exits timely if a end-response notification is received
     * by the ResponseToSyncRequestWaiter
     */
    public void testWakeOnEndWaitingForStart() throws Exception {
        notifyEnd(100);
        startElapsedTime();
        waiter.blockUntilStart();
        verifyElapse(100);
    }
    
    /**
     * Tests that blockUntilEnd exits timely if a end-response notification is received
     * by the ResponseToSyncRequestWaiter
     */
    public void testWakeOnEndWaitingForEnd() throws Exception {
        notifyEnd(100);
        startElapsedTime();
        waiter.blockUntilEnd();
        verifyElapse(100);
    }

    /**
     * Tests that blockUntilEnd exits timely if a start-response notification and an 
     * end-response notification is received by the ResponseToSyncRequestWaiter
     * No data read needed 
     */
    public void testWakeOnStartAndEndWaitingForEnd() throws Exception {
        notifyStart(100);
        notifyEnd(200);
        startElapsedTime();
        waiter.blockUntilEnd();
        verifyElapse(200);
    }

    /**
     * Tests that blockUntilEnd exits due an inactivity timeout if a start-response notification arrives and  
     * no data read or response-end is received after inactivityTime 
     */
    public void testWakeOnStartAndInactivityTimeReachedWaitingForEnd() throws Exception {
        notifyStart(100);
        notifyEnd(400);
        startElapsedTime();
        waiter.blockUntilEnd();
        verifyElapse(100+INACTIVITY_TIMEOUT);
    }

    /**
     * Tests that blockUntilEnd exits timely due an inactivity timeout if a start-response notification arrives and  
     * a data-read notification arrives and no other data-read notification is received after inactivityTime 
     */
    public void testWakeOnStartAndInactivityTimeReachedWaitingForEnd2() throws Exception {
        notifyStart(100);
        notifyDataRead(200);
        notifyEnd(600);
        startElapsedTime();
        waiter.blockUntilEnd();
        verifyElapse(200+INACTIVITY_TIMEOUT);
    }

    
    private void startElapsedTime() {
        tsStart = System.currentTimeMillis();
    }
    
    private void verifyElapse(long expectedTime) {
        long tsEnd =  System.currentTimeMillis();
        long elapsed = (tsEnd - tsStart);
        assertTrue(" elapsed: "+elapsed, Math.abs((elapsed - expectedTime)) < DELTA );
    }
    
    private void notifyStart(final long i) {
        final long waitUntil = System.currentTimeMillis()+i; 
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(Math.max(waitUntil - System.currentTimeMillis(),1));
                    waiter.startOfResponse();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    
    private void notifyEnd(final long i) {
        final long waitUntil = System.currentTimeMillis()+i;
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(Math.max(waitUntil - System.currentTimeMillis(),1));
                    waiter.endOfResponse();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    
    
    private void notifyDataRead(final long i) {
        final long waitUntil = System.currentTimeMillis()+i; 
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(Math.max(waitUntil - System.currentTimeMillis(),1));
                    waiter.dataRead();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    
    
    protected void setUp() throws Exception {
        System.setProperty("com.topcoder.response.start.timeout", "" + START_TIMEOUT);
        System.setProperty("com.topcoder.response.end.timeout", "" + END_TIMEOUT);
        System.setProperty("com.topcoder.response.inactivity.timeout", "" + INACTIVITY_TIMEOUT);
        waiter = new ResponseToSyncRequestWaiter();
    }
}
