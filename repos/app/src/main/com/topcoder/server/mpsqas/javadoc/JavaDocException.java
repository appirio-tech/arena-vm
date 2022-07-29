package com.topcoder.server.mpsqas.javadoc;

/**
 * Used to represent an error which occurred while generating java docs.
 */
public class JavaDocException extends Exception {

    public JavaDocException(String message) {
        super(message);
    }
}
