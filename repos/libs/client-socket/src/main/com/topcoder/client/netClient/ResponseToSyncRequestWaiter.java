/*
 * ResponseToSyncRequestWaiter
 * 
 * Created 03/23/2006
 */
package com.topcoder.client.netClient;

import com.topcoder.shared.util.concurrent.Waiter;

/**
 * Wait object with two wait modes, allowing better control
 * of timeout. 
 * When sending a synchronous request to sever, the calling thread is blocked
 * until the response(s) of the server arrives.
 * This object handles two timeouts values,  one timeout value 
 * (startTimeout) that represents the max time in milliseconds to wait for 
 * an applicable response to be received from the server, and another timeout value (endTimeout)
 * that represents the max time in milliseconds to wait, after the response started, 
 * for the response completation.
 *  
 * @author Diego Belfer (Mural)
 * @version $Id$
 */
public class ResponseToSyncRequestWaiter {
    /**
     * Key of system propery used to look for the default startTimeout
     */
    public static final String START_KEY = "com.topcoder.response.start.timeout";
    
    /**
     * Key of system propery used to look for the default endTimeout
     */
    public static final String END_KEY = "com.topcoder.response.end.timeout";

    /**
     * Key of system propery used to look for the default inactivityTimeout during synch response
     */
    public static final String INACTIVITY_KEY = "com.topcoder.response.inactivity.timeout";
    
    /**
     * Time to wait (in ms) for a start-response or end-response notification
     */
    private long startTimeout;
    
    /**
     * Time to wait (in ms) for an end-response notification after a 
     * start-response notification has been received 
     */
    private long endTimeout;
    
    /**
     * Time to wait (in ms) for a notification of data read on input channel after 
     * start-response notification has been received 
     */
    private long inactivityTimeout;
    /**
     * Flag to indicate that a start-response notification has been received
     */
    private boolean started;
    
    /**
     * Flag to indicate that a end-response notification has been received
     */
    private boolean finished;
    
    /**
     * Flag to indicate that a data has been read from the input channel
     */
    private boolean dataRead;
    
    /**
     * We use this object for wait/notify. We don't want to receive a notify 
     * from a external object
     */
    private Object mutex = new Object();

    /**
     * This flag indicates that a timeout has been set by some thread
     * Hence, blocked thread will be waked up and will receive TimeOut
     */
    private boolean timeout;

    /*
     * CONSTANT USED FOR INPUT CHANNEL MONITORING
     */
    /**
     * Default time in milliseconds to wait for data available on the input channel 
     * when reading or skipping on it
     */
    public final static int INPUT_CHANNEL_INACTIVITY_TIMEOUT_MILLIS = 100 * 1000;

    /**
     * Default time in milliseconds to wait for data available on the input channel
     * during the reading of a response to a synchronous request. 
     * This values is used only after the response has started.
     */
    public final static int RESPONSE_INACTIVITY_TIMEOUT_MILLIS = 60 * 1000;

    /**
     * Default time in milliseconds to wait for the end of response 
     * to a synchronous request after the response has started.
     */
    public final static int RESPONSE_END_TIMEOUT_MILLIS = 30*60*1000;

    /*
     * CONSTANTS USED BY THE RESPONSE WAITER ON SYNCH RESPONSE
     */
    /**
     * Default time in milliseconds to wait for the start of a response 
     * to a synchronous request  
     */
    public final static int RESPONSE_START_TIMEOUT_MILLIS = 30 * 1000;
    
    /**
     * Creates a new ResponseToSyncRequestWaiter with the default timeout values.
     * Default startTimeout is the value defined in the system property with 
     * key <code>START_KEY</code> or if the property is not defined the constant 
     * value <code>ContestConstants.RESPONSE_START_TIMEOUT_MILLIS</code> 
     * Default endTimeout is the value defined in the system property with 
     * key <code>END_KEY</code> or if the property is not defined the constant 
     * value <code>ContestConstants.RESPONSE_END_TIMEOUT_MILLIS</code>
     * Default inactivityTimeout is the value defined in the system property with 
     * key <code>INACTIVITY_KEY</code> or if the property is not defined the constant 
     * value <code>ContestConstants.RESPONSE_INACTIVITY_TIMEOUT_MILLIS</code>  
     */
    public ResponseToSyncRequestWaiter() {
        this(getStartTimeoutDefault(), 
            getEndTimeoutDefault(), getInactivityTimeoutDefault());
    }

    /**
     * Creates a new ResponseToSyncRequestWaiter with the specified timeout values.
     * 
     * @param startTimeout Time to wait (in ms) for a start-response or end-response notification
     * @param endTimeout Time to wait (in ms) for an end-response notification after a 
     *                      start-response notification has been received
     * @param inactivityTimeout Time to wait (in ms) for data in input channel after start-response has been received.
     */
    public ResponseToSyncRequestWaiter(long startTimeout, long endTimeout, long inactivityTimeout) {
        this.startTimeout = startTimeout;
        this.endTimeout = endTimeout;
        this.inactivityTimeout = inactivityTimeout;
    }

    /**
     * Blocks the calling thread until a default notification arrives or
     * the configured timeout is reached
     *    
     * @return true if timeout reached
     * @throws InterruptedException if the the thread was interrupted
     */
    public boolean block() throws InterruptedException {
        return blockUntilEnd();
    }

    /**
     * Unblock all blocked threads on this ResponseToSyncRequestWaiter
     */
    public void unblock() {
        endOfResponse();
    }
    
    /**
     * Blocks the calling thread until an end-response notification arrives or timeout occurs.
     * The thread will be unblocked with timeout if:
     *   1) neither a start-response or end-response notification is received before startTimeout. 
     *      2) a start-response was received before startTimeout but no end-response notification 
     *         is received before endTimeout milliseconds after the arrived of the start-response notification.
     *      3) a start-response was received before startTimeout but no notification of data read has been received
     *         after inactivityeTimeout milliseconds. Neither an end-response notification was received.
     * The thread will be unblocked without timeout if:
     *   4) an end-response notification is received before startTimeout.
     *   5) a start-response is received before startTimeout and an end-response notification 
     *         is received before endTimeout milliseconds after the arrived of the start-response notification.
     *         and inactivityTimeout was never reached
     * 
     * @return true if timeout occurs  (1, 2 or 3)
     * @throws InterruptedException if the the thread was interrupted
     */
    public boolean blockUntilEnd() throws InterruptedException {
        synchronized (mutex) {
            Waiter waiter = new Waiter(startTimeout, mutex);
            while (!started && !finished && !waiter.elapsed() && !timeout) {
                waiter.await();
            }
            if (started && !finished && !timeout) {
                dataRead = true;
                waiter = new Waiter(endTimeout, mutex);
                while (!timeout && !finished && dataRead && !waiter.elapsed()) {
                    long timeToWait = Math.min(Math.max(waiter.getRemaining(), 1), inactivityTimeout);
                    dataRead = false;
                    Waiter waiter2 = new Waiter(timeToWait, mutex);
                    while (!timeout && !finished && !dataRead && !waiter2.elapsed()) {
                        waiter2.await();
                    }
                }
            }
            return timeout || !finished;
        }
    }
    
    /**
     * Blocks the calling thread until an response notification arrives or timeout occurs.
     * The thread will be unblocked with timeout if:
     *   1) neither a start-response or end-response notification is received before startTimeout. 
     * Thread thread will be unblocked without timeout if:
     *   2) otherwise
     * 
     * @return true if timeout occurs (1)
     * @throws InterruptedException if the the thread was interrupted
     */
    public boolean blockUntilStart() throws InterruptedException {
        synchronized (mutex) {
            Waiter waiter = new Waiter(startTimeout, mutex);
            while (!started && !finished && !waiter.elapsed() && !timeout) {
                waiter.await();
            }
            return timeout || !started || !finished;
        }
    }

    /**
     * start-response notification
     * Users of this class must call this method to notify to this ResponseToSyncRequestWaiter
     * that the response has started
     */
    public void startOfResponse() {
        synchronized (mutex) {
            if (started) return;
            started = true;
            mutex.notifyAll();
        }
    }

    /**
     * end-response notification
     * Users of this class must call this method to notify to this ResponseToSyncRequestWaiter
     * that the response has ended
     */
    public void endOfResponse() {
        synchronized (mutex) {
            if (finished) return;
            finished = true;
            mutex.notifyAll();
        }
    }

    /**
     * Force a timeout.
     * Blocked thread will receive a timeout exception 
     */
    public void timeOut() {
        synchronized (mutex) {
            if (finished) return;
            timeout=true;
            mutex.notifyAll();
        }
    }
    
    /**
     * data read  notification
     * Users of this class must call this method to notify to this ResponseToSyncRequestWaiter
     * that the data has been read from the input channel avoiding inactivity timeout.
     */
    public void dataRead() {
        synchronized (mutex) {
            if (!started || finished) return;
            dataRead = true;
            mutex.notifyAll();
        }
    }
    
    /**
     * @return Returns the endTimeout value.
     */
    public long getEndTimeout() {
        return endTimeout;
    }

    /**
     * @return Returns the startTimeout value.
     */
    public long getStartTimeout() {
        return startTimeout;
    }

    /**
     * @return Returns the inactivityTimeout value.
     */
    public long getInactivityTimeout() {
        return inactivityTimeout;
    }
    
    /**
     * @return Returns the default startTimeout value
     */
    public static long getStartTimeoutDefault() {
        return resolveValue(START_KEY, RESPONSE_START_TIMEOUT_MILLIS);
    }

    /**
     * @return Returns the default endTimeout value
     */
    public static long getEndTimeoutDefault() {
        return resolveValue(END_KEY, RESPONSE_END_TIMEOUT_MILLIS);
    }

    /**
     * @return Returns the default endTimeout value
     */
    public static long getInactivityTimeoutDefault() {
        return resolveValue(INACTIVITY_KEY, RESPONSE_INACTIVITY_TIMEOUT_MILLIS);
    }
    
    private static long resolveValue(String key, long defaultValue) {
        String strValue = System.getProperty(key);
        if (strValue != null) {
            return Long.parseLong(strValue);
        }
        return defaultValue;
    }
}
