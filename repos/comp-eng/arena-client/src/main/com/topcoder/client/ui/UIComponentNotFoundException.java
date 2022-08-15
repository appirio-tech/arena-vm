package com.topcoder.client.ui;

/**
 * Defines an exception which is thrown when a UI page cannot found the given UI component inside that page.
 *
 * @version 1.0
 * @author visualage
 */
public class UIComponentNotFoundException extends UIPageException {
    /**
     * Creates a new <code>UIComponentNotFoundException</code> instance with no message and cause.
     */
    public UIComponentNotFoundException() {
    }
    
    /**
     * Creates a new <code>UIComponentNotFoundException</code> instance with a message and no cause.
     * @param message the error message.
     */
    public UIComponentNotFoundException(String message) {
        super(message);
    }
    
    /**
     * Creates a new <code>UIComponentNotFoundException</code> instance with no message and a cause.
     * @param cause the cause of the error.
     */
    public UIComponentNotFoundException(Throwable cause) {
        super(cause);
    }
    
    /**
     * Creates a new <code>UIComponentNotFoundException</code> instance with a message and a cause.
     * @param message the error message.
     * @param cause the cause of the error.
     */
    public UIComponentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
