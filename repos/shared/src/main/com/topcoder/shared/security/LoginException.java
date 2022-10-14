package com.topcoder.shared.security;

/**
 * Indicates a user login attempt failed.
 *
 * @author Ambrose Feinstein
 */
public class LoginException extends AuthenticationException {

    public LoginException(String s) {
        super(s);
    }

    public LoginException(Exception e) {
        super(e.getMessage());
    }
}
