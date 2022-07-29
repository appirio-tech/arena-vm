/**
 * TotalChangeListener.java
 *
 * Description:		Listener for final total changes
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.scoreboard.model;


public interface SubmissionCountListener extends java.util.EventListener {

    /**
     * Signals for the total to be updated
     *
     * @param evt the event
     */
    public void updateCount(SubmissionCountEvent evt);

}


/* @(#)TotalChangeListener.java */
