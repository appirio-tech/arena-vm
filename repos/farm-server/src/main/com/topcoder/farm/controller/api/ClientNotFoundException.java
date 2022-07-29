/*
 * ClientNotFoundException
 * 
 * Created 11/27/2006
 */
package com.topcoder.farm.controller.api;

/**
 * Exception thrown when an action is invoked for a client which does not exists.
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ClientNotFoundException extends Exception {

    public ClientNotFoundException() {
    }

    public ClientNotFoundException(String message) {
        super(message);
    }

    public ClientNotFoundException(Throwable cause) {
        super(cause);
    }

    public ClientNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
