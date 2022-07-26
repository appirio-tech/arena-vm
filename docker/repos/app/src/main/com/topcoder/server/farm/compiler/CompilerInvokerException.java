/*
 * CompilerInvokerException
 * 
 * Created 10/14/2006
 */
package com.topcoder.server.farm.compiler;

/**
 * @author Diego Belfer (mural)
 * @version $Id: CompilerInvokerException.java 54869 2006-12-01 18:02:46Z thefaxman $
 */
public class CompilerInvokerException extends Exception {

    /**
     * 
     */
    public CompilerInvokerException() {
    }

    /**
     * @param message
     */
    public CompilerInvokerException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public CompilerInvokerException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public CompilerInvokerException(String message, Throwable cause) {
        super(message, cause);
    }

}
