/**
 * PointValueChangeListener.java
 *
 * Description:		Listener for changes in an individual point value
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.scoreboard.model;


public interface PointValueChangeListener extends java.util.EventListener {

    /**
     * Notification that a point value change happened
     *
     * @param evt the point value changed event
     */
    public void updatePointValue(PointValueChangeEvent evt);

}


/* @(#)PointValueChangeListener.java */
