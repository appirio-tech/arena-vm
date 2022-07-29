/*
 * InvalidRequirementsException
 * 
 * Created 08/18/2006
 */
package com.topcoder.farm.controller.exception;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class InvalidRequirementsException extends ControllerException {

    private static final long serialVersionUID = 1L;

	public InvalidRequirementsException(String message) {
        super(message);
    }
    
    public InvalidRequirementsException(String message, Throwable cause) {
        super(message, cause);
    }

}
