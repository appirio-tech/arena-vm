/**
 * PhaseEvent.java Description: Contains information when a phase changes
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
package com.topcoder.client.spectatorApp.event;

public class PhaseEvent extends java.util.EventObject {
	
	/** the time allocated to the phase (in seconds) */
	private int timeAllocated;

	/**
	 * Constructor of a Phase Event with unknown time allocated
	 * 
	 * @param source the source of the event
	 */
	public PhaseEvent(Object source) {
		super(source);
		this.timeAllocated = 0;
	}

	/**
	 * Constructor of a Phase Event
	 * 
	 * @param source the source of the event
	 * @param timeAllocated the time (in seconds) allocated to the phase
	 */
	public PhaseEvent(Object source, int timeAllocated) {
		super(source);
		this.timeAllocated = timeAllocated;
	}

	/**
	 * Gets the time allocated to the phase
	 * 
	 * @returns the time (in seconds) allocated to the phase. 0 is return if
	 *          unknown
	 */
	public int getTimeAllocated() {
		return timeAllocated;
	}
}
/* @(#)PhaseEvent.java */
