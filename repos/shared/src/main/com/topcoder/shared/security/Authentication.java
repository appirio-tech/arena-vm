package com.topcoder.shared.security;

/**
 * Interface for classes that implement an authentication scheme, ie, a way to log in.
 *
 * @author Greg Paul
 */
public interface Authentication {

    /** Attempt to log in with the given user. */
    public User login(User u) throws LoginException;

    /** Log out, if currently considered logged in. */
    public void logout();

    /** Get the details of the logged in user if any; otherwise return an anonymous user. */
    public User getUser();
}
