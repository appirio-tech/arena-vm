/**
 * PlacementChangeEvent.java Description: Contains information about a placement change
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
package com.topcoder.client.spectatorApp.scoreboard.model;

public class PlacementChangeEvent extends java.util.EventObject {

	/**
	 * Constructor of a Event
	 * 
	 * @param source the source of the event
	 */
	public PlacementChangeEvent(Object source) {
		super(source);
	}

	/**
	 * Returns the string representation of this event
	 * 
	 * @returns the string representation of this event
	 */
	public String toString() {
		return "(PlacementChangeEvent)";
	}
}

