/*
 * User: Michael Cervantes
 * Date: Sep 4, 2002
 * Time: 4:52:03 AM
 */
package com.topcoder.server.processor;

public class SpecAppProcessorException extends RuntimeException {

    public SpecAppProcessorException(Throwable cause) {
        super(cause);
    }

    public SpecAppProcessorException(String message) {
        super(message);
    }
}
