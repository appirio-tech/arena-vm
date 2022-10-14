/*
 * RoundEventException
 * 
 * Created 10/03/2007
 */
package com.topcoder.shared.round.events;

/**
 * Exception thrown by the round event system when a failure
 * occurs. 
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class RoundEventException extends Exception {

    public RoundEventException() {
    }

    public RoundEventException(String message) {
        super(message);
    }

    public RoundEventException(Throwable cause) {
        super(cause);
    }

    public RoundEventException(String message, Throwable cause) {
        super(message, cause);
    }

}
