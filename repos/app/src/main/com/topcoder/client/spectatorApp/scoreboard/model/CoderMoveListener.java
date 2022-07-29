/**
 * CoderMoveListener.java
 *
 * Description:		Listen for coder moving from problem to problem
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.scoreboard.model;


public interface CoderMoveListener extends java.util.EventListener {

    /**
     * Coder opened a problem
     *
     * @param evt the coder move event
     */
    public void problemOpened(CoderMoveEvent evt);

    /**
     * Coder closed a problem
     *
     * @param evt the coder move event
     */
    public void problemClosed(CoderMoveEvent evt);


}


/* @(#)CoderMoveListener.java */
