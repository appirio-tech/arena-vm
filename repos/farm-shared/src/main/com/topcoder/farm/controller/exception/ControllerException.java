/*
 * ControllerException
 * 
 * Created 05/09/2006
 */
package com.topcoder.farm.controller.exception;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ControllerException extends Exception {
    private static final long serialVersionUID = 1L;

	public ControllerException() {
    }

    public ControllerException(String message) {
        super(message);
    }
    
    public ControllerException(String message, Throwable cause) {
        super(message, cause);
    }
}
