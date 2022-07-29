package com.topcoder.client.ui;

/**
 * Defines an exception which is thrown when the configuration for the UI manager is invalid.
 *
 * @version 1.0
 * @author visualage
 */
public class UIManagerConfigurationException extends UIManagerException {
    /**
     * Creates a new <code>UIManagerConfigurationException</code> instance with no message and cause.
     */
    public UIManagerConfigurationException() {
    }
    
    /**
     * Creates a new <code>UIManagerConfigurationException</code> instance with a message and no cause.
     * @param message the error message.
     */
    public UIManagerConfigurationException(String message) {
        super(message);
    }
    
    /**
     * Creates a new <code>UIManagerConfigurationException</code> instance with no message and a cause.
     * @param cause the cause of the error.
     */
    public UIManagerConfigurationException(Throwable cause) {
        super(cause);
    }
    
    /**
     * Creates a new <code>UIManagerConfigurationException</code> instance with a message and a cause.
     * @param message the error message.
     * @param cause the cause of the error.
     */
    public UIManagerConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
