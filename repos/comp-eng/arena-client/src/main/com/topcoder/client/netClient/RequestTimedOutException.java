package com.topcoder.client.netClient;

/**
 * <p>Title: RequestTimedOutException</p>
 * <p>Description: Thrown when synchronized requests time out.</p>
 * @author Walter Mundt
 */
public class RequestTimedOutException extends Exception {
    /**
     * Creates a new instance of <code>RequestTimedOutException</code>.
     */
    public RequestTimedOutException() {
    }

    /**
     * Creates a new instance of <code>RequestTimedOutException</code>. The error message is given.
     * 
     * @param message the error message.
     */
    public RequestTimedOutException(String message) {
        super(message);
    }
}
