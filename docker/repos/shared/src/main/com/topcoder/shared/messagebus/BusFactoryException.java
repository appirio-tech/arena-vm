/*
 * BusFactoryException
 * 
 * Created Oct 3, 2007
 */
package com.topcoder.shared.messagebus;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class BusFactoryException extends Exception {

    public BusFactoryException() {

    }

    public BusFactoryException(String message) {
        super(message);
    }

    public BusFactoryException(Throwable cause) {
        super(cause);
    }

    public BusFactoryException(String message, Throwable cause) {
        super(message, cause);
    }

}
