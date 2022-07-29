package com.topcoder.client.ui;

/**
 * Defines an exception which is thrown when an operation of UI manager fails.
 *
 * @version 1.0
 * @author visualage
 */
public class UIManagerException extends UIException {
    /**
     * Creates a new <code>UIManagerException</code> instance with no message and cause.
     */
    public UIManagerException() {
    }
    
    /**
     * Creates a new <code>UIManagerException</code> instance with a message and no cause.
     * @param message the error message.
     */
    public UIManagerException(String message) {
        super(message);
    }
    
    /**
     * Creates a new <code>UIManagerException</code> instance with no message and a cause.
     * @param cause the cause of the error.
     */
    public UIManagerException(Throwable cause) {
        super(cause);
    }
    
    /**
     * Creates a new <code>UIManagerException</code> instance with a message and a cause.
     * @param message the error message.
     * @param cause the cause of the error.
     */
    public UIManagerException(String message, Throwable cause) {
        super(message, cause);
    }
}
