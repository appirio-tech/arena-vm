/**
 * LoginSupport.java
 *
 * Description:		Event set support class for LoginListener.
 *					Manages listener registration and contains fire functions.
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.event;


import java.util.ArrayList;

/**
 * LoginSupport bottlenecks support for classes that fire events to
 * LoginListener listeners.
 */

public class LoginSupport {

    /** Holder for all listeners */
    private ArrayList loginListeners = new ArrayList();

    /**
     * Adds a listener
     *
     * @param listener the listener to be added
     */
    public synchronized void addLoginListener(LoginListener listener) {
        // add a listener if it is not already registered
        if (!loginListeners.contains(listener)) {
            loginListeners.add(listener);
        }
    }

    /**
     * Removes a listener
     *
     * @param listener the listener to be removed
     */
    public synchronized void removeLoginListener(LoginListener listener) {
        // remove it if it is registered
        int pos = loginListeners.indexOf(listener);
        if (pos >= 0) {
            loginListeners.remove(pos);
        }
    }

    /**
     *  Notifies all listeners that the login was successful
     *
     *  @param event   the event to send to the listener
     */
    public synchronized void fireLoginSuccessful(LoginEvent event) {
        // Fire the event to all listeners (done in reverse order from how they were added).
        for (int i = loginListeners.size() - 1; i >= 0; i--) {
            LoginListener listener = (LoginListener) loginListeners.get(i);
            listener.loginSuccessful(event);
        }
    }

    /**
     *  Notifies all listeners that the login failed
     *
     *  @param event   the event to send to the listener
     */
    public synchronized void fireLoginFailure(LoginEvent event) {
        // Fire the event to all listeners (done in reverse order from how they were added).
        for (int i = loginListeners.size() - 1; i >= 0; i--) {
            LoginListener listener = (LoginListener) loginListeners.get(i);
            listener.loginFailure(event);
        }
    }


}

/* @(#)LoginSupport.java */
