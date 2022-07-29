package com.topcoder.client.netClient;

import com.topcoder.netCommon.contest.ContestConstants;

/**
 * Defines a helper class which is used to wait for the server response for a synchronized client request.
 * 
 * @author Qi Liu
 * @version $Id: ResponseWaiter.java 72046 2008-07-31 06:47:43Z qliu $
 */
public final class ResponseWaiter {
    /**
     * Represents a flag indicating if the response has been received or not.
     */
    private boolean responseReceived;

    /**
     * Represents the timeout to receive the response.
     */
    private static long TIMEOUT = Long.parseLong(System.getProperty("com.topcoder.response.timeout", String.valueOf(ContestConstants.TIMEOUT_MILLIS)));
    
    /**
     * Blocks till either unBlock() is called or the amount of time (<code>TIMEOUT</code>)
     * has elapsed.
     *
     * @return true if timeout
     */
    public synchronized boolean block() {
        responseReceived = false;
        long start = System.currentTimeMillis();
        long end = start + TIMEOUT;
        while (!responseReceived) {
            try {
                long timeout = Math.max(1, end - System.currentTimeMillis());
                wait(timeout);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (System.currentTimeMillis() >= end) {
                return true;
            }
        }
        return false;
    }

    /**
     * Stops waiting for the response. Pretend the response has been received.
     */
    public synchronized void unBlock() {
        responseReceived = true;
        notifyAll();
    }

}
