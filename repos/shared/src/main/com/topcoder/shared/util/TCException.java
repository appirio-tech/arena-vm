package com.topcoder.shared.util;

/**
 * User: dok
 * Date: Dec 10, 2004
 */
public class TCException extends Exception {

    /**
     * Default Constructor
     */
    public TCException() {
        super();
    }

    /**
     * <p>
     * Constructor taking a string message
     * </p>
     *
     * @param message - the message of the exception
     */
    public TCException(String message) {
        super(message);
    }

    /**
     * <p>
     * Constructor taking a nested exception
     * </p>
     *
     * @param nestedException the nested exception
     */
    public TCException(Throwable nestedException) {
        super(nestedException);
    }

    /**
     * <p>
     * Constructor taking a nested exception and a string
     * </p>
     *
     * @param message the message of this exception
     * @param nestedException the nested exception
     */
    public TCException(String message, Throwable nestedException) {
        super(message, nestedException);
    }

}


