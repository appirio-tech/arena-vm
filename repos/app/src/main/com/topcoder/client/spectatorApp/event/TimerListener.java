/**
 * TimerListener.java
 *
 * Description:		Interface for timer updates
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.event;


public interface TimerListener extends java.util.EventListener {

    /**
     * Method called on an update of the timer
     *
     * @param evt associated event
     */
    public void timerUpdate(TimerEvent evt);

}


/* @(#)TimerListener.java */
