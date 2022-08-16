package com.topcoder.client.ui;

/**
 * Defines an exception which is thrown when an operation of UI page fails.
 *
 * @version 1.0
 * @author visualage
 */
public class UIPageException extends UIException {
    /**
     * Creates a new <code>UIPageException</code> instance with no message and cause.
     */
    public UIPageException() {
    }
    
    /**
     * Creates a new <code>UIPageException</code> instance with a message and no cause.
     * @param message the error message.
     */
    public UIPageException(String message) {
        super(message);
    }
    
    /**
     * Creates a new <code>UIPageException</code> instance with no message and a cause.
     * @param cause the cause of the error.
     */
    public UIPageException(Throwable cause) {
        super(cause);
    }
    
    /**
     * Creates a new <code>UIPageException</code> instance with a message and a cause.
     * @param message the error message.
     * @param cause the cause of the error.
     */
    public UIPageException(String message, Throwable cause) {
        super(message, cause);
    }
}
