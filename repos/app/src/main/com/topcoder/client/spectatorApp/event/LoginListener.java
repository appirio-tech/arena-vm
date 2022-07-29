/**
 * LoginListener.java
 *
 * Description:		Interface for a Login events
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.event;


public interface LoginListener extends java.util.EventListener {

    /**
     * Method called on a successful login
     *
     * @param evt associated login event
     */
    public void loginSuccessful(LoginEvent evt);

    /**
     * Method called on a failed login
     *
     * @param evt associated login event
     */
    public void loginFailure(LoginEvent evt);


}


/* @(#)LoginListener.java */
