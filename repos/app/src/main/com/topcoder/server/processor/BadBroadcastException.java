package com.topcoder.server.processor;

/**
 * <p>Title: BadBroadcastException</p>
 * <p>Description: </p>
 * @author Walter Mundt
 */
public class BadBroadcastException extends Exception {

    public BadBroadcastException() {
        super();
    }

    public BadBroadcastException(String message) {
        super(message);
    }
/*    public BadBroadcastException(String message, Throwable cause) {
        super(message, cause);
    }
    public BadBroadcastException(Throwable cause) { -- oops, these are for 1.4
        super(cause);
    }*/
}
