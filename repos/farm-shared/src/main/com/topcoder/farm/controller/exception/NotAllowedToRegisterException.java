/*
 * NotAllowedToRegisterException
 * 
 * Created 09/05/2006
 */
package com.topcoder.farm.controller.exception;

/**
 * Exception thrown when a node tries to register with a controller
 * but the controller is configured to reject registration for that node
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class NotAllowedToRegisterException extends ControllerException {

    public NotAllowedToRegisterException() {
    }

    public NotAllowedToRegisterException(String message) {
        super(message);
    }
}
