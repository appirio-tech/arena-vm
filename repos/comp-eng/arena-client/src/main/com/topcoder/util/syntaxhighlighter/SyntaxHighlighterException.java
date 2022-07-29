/*
 * Copyright (C) 2005 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.util.syntaxhighlighter;

import com.topcoder.util.errorhandling.BaseException;


/**
 * <p>
 * This is a generic exception that is inherited by all exceptions thrown by this component. An instance of this
 * (base) exception is not directly thrown by the component but is provided by generic exception handling (i.e.
 * catch SyntaxHighlighterException instead of each of the individual exceptions).
 * </p>
 *
 * @author duner, still
 * @version 2.0
 */
public class SyntaxHighlighterException extends BaseException {
    /**
     * <p>Constructs a new SyntaxHighlighterException.</p>
     *
     */
    public SyntaxHighlighterException() {
    }

    /**
     * <p>Constructs a new SyntaxHighlighterException, with the given message.</p>
     *
     *
     * @param message A message describing the cause of the exception.
     */
    public SyntaxHighlighterException(String message) {
        super(message);
    }

    /**
     * <p>Constructs a new SyntaxHighlighterException, with the given message and cause.</p>
     *
     *
     * @param message A message describing the cause of the exception.
     * @param cause Throwable cause of the exception.
     */
    public SyntaxHighlighterException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * <p>Constructs a new SyntaxHighlighterException, with the given cause.</p>
     *
     *
     * @param cause Throwable cause of the exception.
     */
    public SyntaxHighlighterException(Throwable cause) {
        super(cause);
    }
}
