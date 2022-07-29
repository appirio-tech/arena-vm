/*
 * ConfigurationNotFoundException
 * 
 * Created Oct 6, 2007
 */
package com.topcoder.shared.messagebus.jms;

/**
 * Exception indicating that not matching configuration was found.
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ConfigurationNotFoundException extends Exception {

    public ConfigurationNotFoundException() {
    }

    public ConfigurationNotFoundException(String message) {
        super(message);
    }

    public ConfigurationNotFoundException(Throwable cause) {
        super(cause);
    }

    public ConfigurationNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
