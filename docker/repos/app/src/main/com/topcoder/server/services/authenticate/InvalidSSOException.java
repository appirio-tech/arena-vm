/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.server.services.authenticate;

/**
 * The invalid SSO exception.
 *
 * <p>
 * Changes in version 1.2 (TopCoder Competition Engine - Revise Authentication Logic for SSO v1.0):
 * <ol>
 *      <li>Added {@link #InvalidSSOException(String)} constructor.</li>
 *      <li>Added {@link #InvalidSSOException(String, Throwable)} constructor.</li>
 * </ol>
 * </p>
 *
 * @author TCSASSEMBLER
 * @version 1.2
 */
public class InvalidSSOException extends Exception {
    /**
     * The constructor with message.
     *
     * @param message the message.
     * @since 1.2
     */
    public InvalidSSOException(String message) {
        super(message);
    }

    /**
     * The constructor with message and inner cause.
     *
     * @param message the message.
     * @param e the cause.
     * @since 1.2
     */
    public InvalidSSOException(String message, Throwable e) {
        super(message, e);
    }
}
