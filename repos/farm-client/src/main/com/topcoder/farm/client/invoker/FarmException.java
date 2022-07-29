/*
 * FarmException
 * 
 * Created 08/11/2006
 */
package com.topcoder.farm.client.invoker;

/**
 * FarmException is the base class for all exceptions generated during farm processing
 * that may be thrown while processing invocation.
 * 
 * NOTE:
 * Exception thrown by the Invocation object itself  are reported inside the InvocationResponse 
 * object.
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class FarmException extends Exception {
    public FarmException() {
        super();
    }

    public FarmException(String message, Throwable cause) {
        super(message, cause);
    }

    public FarmException(String message) {
        super(message);
    }

    public FarmException(Throwable cause) {
        super(cause);
    }
}
