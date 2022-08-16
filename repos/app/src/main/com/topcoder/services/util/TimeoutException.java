package com.topcoder.services.util;

/**
 * TimeoutException.java
 *
 * Created on October 29, 2001
 */

/**
 * Exception to be thrown when the users code times out.
 *
 * @author Alex Roman
 * @version 1.0
 */
public final class TimeoutException extends Exception {

    //public TimeoutException() { super(); }
    public TimeoutException(String msg) {
        super(msg);
    }
}
