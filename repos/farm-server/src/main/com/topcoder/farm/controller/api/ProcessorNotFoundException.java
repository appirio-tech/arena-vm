/*
 * ProcessorNotFoundException
 * 
 * Created 11/27/2006
 */
package com.topcoder.farm.controller.api;

/**
 * Exception thrown when an action is invoked for a processor which does not exists.
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ProcessorNotFoundException extends Exception {

    public ProcessorNotFoundException() {
    }

    public ProcessorNotFoundException(String message) {
        super(message);
    }

    public ProcessorNotFoundException(Throwable cause) {
        super(cause);
    }

    public ProcessorNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
