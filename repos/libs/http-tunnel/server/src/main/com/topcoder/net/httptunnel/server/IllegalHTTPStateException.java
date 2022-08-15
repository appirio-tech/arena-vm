/*
 * IllegalHTTPStateException
 *
 * Created 04/06/2007
 */
package com.topcoder.net.httptunnel.server;

/**
 * Exception thrown when an action is attemptted on a HTTPConnection
 * that is not in the proper state.<p>
 *
 * Eg: Send a final chunk when no initial chunk message was sent.
 *
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class IllegalHTTPStateException extends Exception {

    public IllegalHTTPStateException() {
    }

    public IllegalHTTPStateException(String message) {
        super(message);
    }

    public IllegalHTTPStateException(Throwable cause) {
        super(cause);
    }

    public IllegalHTTPStateException(String message, Throwable cause) {
        super(message, cause);
    }
}
