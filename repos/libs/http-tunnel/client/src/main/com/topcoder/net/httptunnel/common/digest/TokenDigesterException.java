/*
 * TokenDigesterException Created 04/05/2007
 */
package com.topcoder.net.httptunnel.common.digest;

/**
 * Defines an exception which is thrown when the generating digest of the token failed.
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class TokenDigesterException extends RuntimeException {
    /**
     * Creates a new instance of <code>TokenDigesterException</code> class. No additional information
     * is given.
     */
    public TokenDigesterException() {
        super();
    }

    /**
     * Creates a new instance of <code>TokenDigesterException</code> class. The cause and a description
     * of the failure are given.
     * 
     * @param message the description of the failure.
     * @param cause the cause of the failure.
     */
    public TokenDigesterException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new instance of <code>TokenDigesterException</code> class. The description
     * of the failure is given.
     * 
     * @param message the description of the failure.
     */
    public TokenDigesterException(String message) {
        super(message);
    }
}
