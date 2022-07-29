/*
 * Copyright (C) 2005 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.util.syntaxhighlighter;


/**
 * <p>
 * Thrown by HighlightedOutput subclasses to signal some problem that could not be overcome while highlighting.
 * This is propagated from HighlightedOutput through SyntaxHighlighter
 * </p>
 *
 * @author duner, still
 * @version 2.0
 */
public class HighlightingException extends com.topcoder.util.syntaxhighlighter.SyntaxHighlighterException {
    /**
     * <p>Constructs a new HighlightingException.</p>
     *
     */
    public HighlightingException() {
    }

    /**
     * <p>Constructs a new HighlightingException, with the given message.</p>
     *
     *
     * @param message A message describing the cause of the exception.
     */
    public HighlightingException(String message) {
        super(message);
    }

    /**
     * <p>Constructs a new HighlightingException, with the given message and cause.</p>
     *
     *
     * @param message A message describing the cause of the exception.
     * @param cause Throwable cause of the exception.
     */
    public HighlightingException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * <p>Constructs a new HighlightingException, with the given cause.</p>
     *
     *
     * @param cause Throwable cause of the exception.
     */
    public HighlightingException(Throwable cause) {
        super(cause);
    }
}
