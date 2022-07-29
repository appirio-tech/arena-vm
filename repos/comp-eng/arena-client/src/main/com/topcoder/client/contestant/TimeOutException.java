package com.topcoder.client.contestant;

/**
 * <p>Title: RequestTimedOutException</p>
 * <p>Description: Thrown when synchronized requests time out.</p>
 * @author Walter Mundt
 */
public class TimeOutException extends Exception {
    /**
     * Creates a new instance of <code>TimeOutException</code>. The detailed error message is given.
     * 
     * @param message the error message.
     */
    public TimeOutException(String message) {
        super(message);
    }

    /**
     * Creates a new instance of <code>TimeOutException</code>. The cause of the error is given.
     * @param parent the cause of the error.
     */
    public TimeOutException(Exception parent) {
        super(parent);
    }
}
