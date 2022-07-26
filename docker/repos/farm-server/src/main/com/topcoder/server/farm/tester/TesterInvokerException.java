/*
 * TesterInvokerException
 * 
 * Created 05/01/2007
 */
package com.topcoder.server.farm.tester;

/**
 * @autor Diego Belfer (Mural)
 * @version $Id: TesterInvokerException.java 56700 2007-01-29 21:13:11Z thefaxman $
 */
public class TesterInvokerException extends Exception {

    /**
     * 
     */
    public TesterInvokerException() {
    }

    /**
     * @param message
     */
    public TesterInvokerException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public TesterInvokerException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public TesterInvokerException(String message, Throwable cause) {
        super(message, cause);
    }

}
