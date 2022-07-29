/*
 * InvocationException
 * 
 * Created 09/13/2006
 */
package com.topcoder.farm.shared.invocation;

/**
 * Exceptiong thrown by Invocation object is some exception is generated
 * when trying to execute.
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class InvocationException extends Exception {

    public InvocationException() {
    }

    public InvocationException(String message) {
        super(message);
    }

    public InvocationException(Throwable cause) {
        super(cause);
    }

    public InvocationException(String message, Throwable cause) {
        super(message, cause);
    }

}
