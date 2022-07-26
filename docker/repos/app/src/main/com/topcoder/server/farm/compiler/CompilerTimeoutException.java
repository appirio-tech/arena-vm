/*
 * CompilerTimeoutException
 * 
 * Created 10/26/2006
 */
package com.topcoder.server.farm.compiler;

/**
 * Exception thrown when no response has been received for a compilation 
 * request after the max wait time has been reached.
 *
 * @author Diego Belfer (mural)
 * @version $Id: CompilerTimeoutException.java 54869 2006-12-01 18:02:46Z thefaxman $
 */
public class CompilerTimeoutException extends CompilerInvokerException {

    public CompilerTimeoutException() {
    }

    public CompilerTimeoutException(String message) {
        super(message);
    }

    public CompilerTimeoutException(Throwable cause) {
        super(cause);
    }
    public CompilerTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}
