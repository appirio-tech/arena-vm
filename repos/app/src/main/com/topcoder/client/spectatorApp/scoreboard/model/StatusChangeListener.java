/**
 * StatusChangeListener.java
 *
 * Description:		Listener when the status of a problem changes
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.scoreboard.model;


public interface StatusChangeListener extends java.util.EventListener {

    /**
     * Notifies of an update in the status of a problem
     *
     * @param evt the status event
     */
    public void updateStatus(StatusChangeEvent evt);

}


/* @(#)StatusChangeListener.java */
