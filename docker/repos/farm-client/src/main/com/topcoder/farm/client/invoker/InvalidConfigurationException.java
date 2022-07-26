/*
 * InvalidConfigurationException
 * 
 * Created 08/15/2006
 */
package com.topcoder.farm.client.invoker;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class InvalidConfigurationException extends FarmException {

    public InvalidConfigurationException() {
        super();
    }

    public InvalidConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidConfigurationException(String message) {
        super(message);
    }

    public InvalidConfigurationException(Throwable cause) {
        super(cause);
    }
}
