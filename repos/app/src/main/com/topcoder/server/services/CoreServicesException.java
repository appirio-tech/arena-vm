/*
 * CoreServicesException
 *
 * Created 03/15/2007
 */
package com.topcoder.server.services;

/**
 * @author Diego Belfer (mural)
 * @version $Id: CoreServicesException.java 59940 2007-04-17 16:20:14Z thefaxman $
 */
public class CoreServicesException extends Exception {

    public CoreServicesException() {
    }

    public CoreServicesException(String message) {
        super(message);
    }

    public CoreServicesException(Throwable cause) {
        super(cause);
    }

    public CoreServicesException(String message, Throwable cause) {
        super(message, cause);
    }

}
