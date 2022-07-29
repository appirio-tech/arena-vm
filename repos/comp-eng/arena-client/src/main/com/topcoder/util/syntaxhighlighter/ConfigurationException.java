/*
 * Copyright (C) 2005 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.util.syntaxhighlighter;


/**
 * <p>
 * Thrown by SyntaxHighlighter to indicate a problem with Configuration and propagated from Language, Category and
 * Rule in case of some problem with the Configuration files.
 * </p>
 * @author duner, still
 * @version 2.0
 */
public class ConfigurationException extends SyntaxHighlighterException {
    /**
     * <p>Constructs a new ConfigurationException.</p>
     *
     */
    public ConfigurationException() {
        // empty constructor
    }

    /**
     * <p>Constructs a new ConfigurationException, with the given message.</p>
     *
     *
     * @param message A message describing the cause of the exception.
     */
    public ConfigurationException(String message) {
        super(message);
    }

    /**
     * <p>Constructs a new ConfigurationException, with the given message and cause.</p>
     *
     *
     * @param message A message describing the cause of the exception.
     * @param cause Throwable cause of the exception.
     */
    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * <p>Constructs a new ConfigurationException, with the given cause.</p>
     *
     *
     * @param cause Throwable cause of the exception.
     */
    public ConfigurationException(Throwable cause) {
        super(cause);
    }
}
