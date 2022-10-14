/*
 * BusException
 * 
 * Created Oct 3, 2007
 */
package com.topcoder.shared.messagebus;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class BusException extends Exception {

    public BusException() {
    }

    public BusException(String message) {
        super(message);
    }

    public BusException(Throwable cause) {
        super(cause);
    }

    public BusException(String message, Throwable cause) {
        super(message, cause);
    }
}
