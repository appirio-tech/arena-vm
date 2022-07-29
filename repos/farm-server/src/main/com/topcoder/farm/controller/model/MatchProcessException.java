/*
 * MatchProcessException
 * 
 * Created 10/12/2006
 */
package com.topcoder.farm.controller.model;

/**
 * A MatchProcessException is thrown when it is not possible to 
 * process match request.  
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class MatchProcessException extends Exception {

    public MatchProcessException() {
    }

    public MatchProcessException(String message) {
        super(message);
    }

    public MatchProcessException(Throwable cause) {
        super(cause);
    }

    public MatchProcessException(String message, Throwable cause) {
        super(message, cause);
    }

}
