package com.topcoder.client.ui;

/**
 * Defines a base exception of the TopCoder UI Design framework.
 *
 * @version 1.0
 * @author visualage
 */
public class UIException extends RuntimeException {
    /**
     * Creates a new <code>UIException</code> instance with no message and cause.
     */
    public UIException() {
    }
    
    /**
     * Creates a new <code>UIException</code> instance with a message and no cause.
     * @param message the error message.
     */
    public UIException(String message) {
        super(message);
    }
    
    /**
     * Creates a new <code>UIException</code> instance with no message and a cause.
     * @param cause the cause of the error.
     */
    public UIException(Throwable cause) {
        super(cause);
    }
    
    /**
     * Creates a new <code>UIException</code> instance with a message and a cause.
     * @param message the error message.
     * @param cause the cause of the error.
     */
    public UIException(String message, Throwable cause) {
        super(message, cause);
    }
}
