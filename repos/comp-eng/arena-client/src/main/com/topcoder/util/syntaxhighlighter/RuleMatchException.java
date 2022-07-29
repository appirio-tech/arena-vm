/*
 * Copyright (C) 2005 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.util.syntaxhighlighter;


/**
 * <p>
 * RuleMatchException is thrown if a Rule encounters an unrecoverable error while attempting to perform the
 * matching. Certain rules may have certain criteria that may not be met that is an exceptional condition. None of
 * the current Rule subclasses throw this exception currently.
 * </p>
 *
 * @author duner, still
 * @version 2.0
 */
public class RuleMatchException extends SyntaxHighlighterException {
    /**
     * <p>Constructs a new RuleMatchException.</p>
     *
     */
    public RuleMatchException() {
    }

    /**
     * <p>Constructs a new RuleMatchException, with the given message.</p>
     *
     *
     * @param message A message describing the cause of the exception.
     */
    public RuleMatchException(String message) {
        super(message);
    }

    /**
     * <p>Constructs a new RuleMatchException, with the given message and cause.</p>
     *
     *
     * @param message A message describing the cause of the exception.
     * @param cause Throwable cause of the exception.
     */
    public RuleMatchException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * <p>Constructs a new RuleMatchException, with the given cause.</p>
     *
     *
     * @param cause Throwable cause of the exception.
     */
    public RuleMatchException(Throwable cause) {
        super(cause);
    }
}
