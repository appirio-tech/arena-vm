/*
 * @(#)BaseError.java
 *
 * Copyright (c) 2003, TopCoder, Inc. All rights reserved
 */

package com.topcoder.util.errorhandling;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * This class is intended to be used as the superclass of all
 * <code>Error</code> classes in an application. It provides extended
 * functionality similar to what is provided in JDK 1.4; this class
 * however, works under JDK 1.2 and later.
 *
 * @author TCSDESIGNER, Sleeve
 * @version 1.0
 * @see BaseException
 * @see BaseRuntimeException
 */
public class BaseError extends Error {

    /* The cause of this BaseRuntimeException */
    private Throwable cause;
    
    /* 
     * Has cause been initialized? Needed to determine if we throw an
     * IllegalStateException
     */
    private boolean isCauseInitialized = false;

    /**
     * Constructs a new <code>BaseError</code>.
     */
    public BaseError() {
    }

    /**
     * Constructs a new <code>BaseError</code>, with the
     * given message.
     *
     * @param message descriptive message
     */
    public BaseError(final String message) {
        super(message);
    }

    /**
     * Constructs a new <code>BaseError</code>, with the
     * given message and cause.
     *
     * @param message descriptive message
     * @param cause <code>Throwable</code> cause of this exception
     */
    public BaseError(final String message, final Throwable cause) {
        super(message);

        this.cause = cause;
        this.isCauseInitialized = true;
    }

    /**
     * Constructs a new <code>BaseError</code>, with the given cause.
     *
     * @param cause <code>Throwable</code> cause of this exception
     */
    public BaseError(final Throwable cause) {

        /* Set the message to cause.toString() if it is not null */ 
        super((cause == null) ? null : cause.toString());

        this.cause = cause;
        this.isCauseInitialized = true;
    }

    /**
     * Get the cause of this BaseError.
     *
     * @return the <code>Throwable</code> cause of this error, or
     *  <code>null</code> if there is none
     */
    public Throwable getCause() {
        return this.cause;
    }

    /**
     * Sets the cause of this exception, if it had not been set/initialized
     * before.
     *
     * @param cause <code>Throwable</code> cause of this error
     * @return a reference to this instance
     * @throws IllegalArgumentException if <code>cause</code> is the same
     *  as this instance; this can't be it's own cause
     * @throws IllegalStateException if <code>cause</code> was already
     *  specified in the constructor or by <code>initCause</code> previously
     */
    public Throwable initCause(final Throwable cause) {
        if (cause == this) {
            throw new IllegalArgumentException("this cannot be its own cause");
        }

        if (this.isCauseInitialized) {
            throw new IllegalStateException(
                        "cause was previously initialized or set");
        }

        this.isCauseInitialized = true;
        this.cause = cause;

        return this;
    }

    /**
     * Returns the message for this error. This includes the message
     * for this error and all associated causes, separated by the
     * String <code>", caused by "</code>.
     * <p>
     * An example of what might be returned:<br />
     * <code>"Fatal error in application, caused by No connection to
     * database"</code>
     *
     * @return message for this error
     */
    public String getMessage() {
        String result = super.getMessage();

        if (cause != null) {
            result += ", caused by " + cause.getMessage();
        }

        return result;
    }

    /**
     * Prints this error's stack trace to <code>System.err</code>.
     *
     * @see #printStackTrace(PrintWriter)
     */
    public void printStackTrace() {
        printStackTrace(System.err);
    }

    /**
     * Prints this error's stack trace to the given <code>PrintStream</code>.
     *
     * @param ps <code>PrintStream</code> to which stack trace is written
     * @throws NullPointerException if argument is null
     * @see #printStackTrace(PrintWriter)
     */
    public void printStackTrace(final PrintStream ps) {
        if (ps == null) {
            throw new NullPointerException("PrintStream cannot be null");
        }

        super.printStackTrace(ps);
        
        /* Print out the cause's stacktrace as well */
        if (this.cause != null) {
            ps.print("Caused by:");
            this.cause.printStackTrace(ps);
        }
    }

    /**
     * Prints this error's stack trace to the given
     * <code>PrintWriter</code>.
     * <p>
     * The stack trace consists of this error's stack trace, plus
     * the stack traces of all causes, separated by the String
     * <code>"Caused by: "</code>.
     * <p>
     * An example of what might be written:<br />
     * <pre>
     * com.acme.MyError: Message one
     *  at Test.main(Test.java:4)
     * Caused by: java.lang.Exception: Other message
     *  at Test.main(Test.java:3)
     * </pre>
     *
     * @param pw <code>PrintWriter</code> to which stack trace is written
     * @throws NullPointerException if argument is null
     */
    public void printStackTrace(final PrintWriter pw) {
        if (pw == null) {
            throw new NullPointerException("PrintWriter cannot be null");
        }

        super.printStackTrace(pw);
        
        /* Print out the cause's stacktrace as well */
        if (this.cause != null) {
            pw.print("Caused by:");
            this.cause.printStackTrace(pw);
        }
    }
}