package com.topcoder.client.ui;

/**
 * Defines an exception which is thrown by UI components. It represents the failure of component operation.
 *
 * @version 1.0
 * @author visualage
 */
public class UIComponentException extends UIException {
    /**
     * Creates a new <code>UIComponentException</code> instance with no message and cause.
     */
    public UIComponentException() {
    }
    
    /**
     * Creates a new <code>UIComponentException</code> instance with a message and no cause.
     * @param message the error message.
     */
    public UIComponentException(String message) {
        super(message);
    }
    
    /**
     * Creates a new <code>UIComponentException</code> instance with no message and a cause.
     * @param cause the cause of the error.
     */
    public UIComponentException(Throwable cause) {
        super(cause);
    }
    
    /**
     * Creates a new <code>UIComponentException</code> instance with a message and a cause.
     * @param message the error message.
     * @param cause the cause of the error.
     */
    public UIComponentException(String message, Throwable cause) {
        super(message, cause);
    }
}
