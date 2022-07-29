package com.topcoder.server.listener.nio.channels;

/**
 * Unchecked exception thrown when an attempt is made to use a selection key that is no longer valid.
 */
public final class CancelledKeyException extends IllegalStateException {

    /**
     * Constructs an instance of this class.
     */
    public CancelledKeyException() {
    }

}
