/**
 * TimerEvent.java
 *
 * Description:		Contains information about the time left in the current phase.
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.event;

public class TimerEvent extends java.util.EventObject {

    /** The time left (in seconds) of the current event */
    private int timeLeft;

    /**
     *  Constructor of a Timer Event
     *
     *  @param source   the source of the event
     *  @param timeLeft the time left (in seconds) of the current phase
     */
    public TimerEvent(Object source, int timeLeft) {
        super(source);
        this.timeLeft = timeLeft;
    }

    /**
     * Gets the time left (in seconds) of the current phase
     * @returns the time (in seconds)
     */
    public int getTimeLeft() {
        return timeLeft;
    }

}


/* @(#)TimerEvent.java */
