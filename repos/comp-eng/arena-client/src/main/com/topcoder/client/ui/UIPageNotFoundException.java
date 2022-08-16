package com.topcoder.client.ui;

/**
 * Defines an exception which is thrown when a UI page does not exist in the UI manager.
 *
 * @version 1.0
 * @author visualage
 */
public class UIPageNotFoundException extends UIManagerException {
    /**
     * Creates a new <code>UIPageNotFoundException</code> instance with no message and cause.
     */
    public UIPageNotFoundException() {
    }
    
    /**
     * Creates a new <code>UIPageNotFoundException</code> instance with a message and no cause.
     * @param message the error message.
     */
    public UIPageNotFoundException(String message) {
        super(message);
    }
    
    /**
     * Creates a new <code>UIPageNotFoundException</code> instance with no message and a cause.
     * @param cause the cause of the error.
     */
    public UIPageNotFoundException(Throwable cause) {
        super(cause);
    }
    
    /**
     * Creates a new <code>UIPageNotFoundException</code> instance with a message and a cause.
     * @param message the error message.
     * @param cause the cause of the error.
     */
    public UIPageNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
