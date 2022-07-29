/**
 * TotalChangeListener.java
 *
 * Description:		Listener for final total changes
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.scoreboard.model;


public interface TotalChangeListener extends java.util.EventListener {

    /**
     * Signals for the total to be updated
     *
     * @param evt the event
     */
    public void updateTotal(TotalChangeEvent evt);

}


/* @(#)TotalChangeListener.java */
