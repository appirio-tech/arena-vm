package com.topcoder.shared.security;

/**
 * Thrown by methods in Authentication, Authorization, etc.
 *
 * @author Greg Paul, Ambrose Feinstein
 */
public class AuthenticationException extends Exception {

    public AuthenticationException(String s) {
        super(s);
    }

    public AuthenticationException(Exception e) {
        super(e.getMessage());
    }
}
