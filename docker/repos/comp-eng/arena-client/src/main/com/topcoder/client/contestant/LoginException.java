/**
 * @author Michael Cervantes (emcee)
 * @since May 8, 2002
 */
package com.topcoder.client.contestant;

/**
 * Defines an error which is thrown when the login process fails.
 * @author Michael Cervantes (emcee)
 * @version $Id: LoginException.java 71798 2008-07-22 09:26:36Z qliu $
 */
public class LoginException extends Exception {
    /**
     * Creates a new instance of <code>LoginException</code>. The error message is given.
     * @param msg the error message.
     */
    public LoginException(String msg) {
        super(msg);
    }
}
