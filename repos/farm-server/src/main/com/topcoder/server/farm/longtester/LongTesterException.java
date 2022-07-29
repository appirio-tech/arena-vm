/*
 * LongTesterException
 * 
 * Created 14/09/2006
 */
package com.topcoder.server.farm.longtester;

/**
 * @author Diego Belfer (mural)
 * @version $Id: LongTesterException.java 54869 2006-12-01 18:02:46Z thefaxman $
 */
public class LongTesterException extends Exception {

    /**
     * 
     */
    public LongTesterException() {
    }

    /**
     * @param message
     */
    public LongTesterException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public LongTesterException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public LongTesterException(String message, Throwable cause) {
        super(message, cause);
    }

}
