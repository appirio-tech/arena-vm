/*
 * LongTestServiceException
 * 
 * Created 20/04/2006
 */
package com.topcoder.server.ejb.TestServices.longtest;

/**
 * @author Diego Belfer (mural)
 * @version $Id: LongTestServiceException.java 45114 2006-05-10 16:30:47Z thefaxman $
 */
public class LongTestServiceException extends Exception {

    public LongTestServiceException() {
        super();
    }

    public LongTestServiceException(String message) {
        super(message);
    }
    
    public LongTestServiceException(Throwable cause) {
    	super(cause);
    }
}
