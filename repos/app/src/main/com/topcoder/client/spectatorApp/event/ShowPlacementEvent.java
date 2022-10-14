/**
 * Description: Contains information about what placements to show
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
package com.topcoder.client.spectatorApp.event;

import com.topcoder.client.spectatorApp.CommonRoutines;

public class ShowPlacementEvent extends java.util.EventObject {

	private int[] showPlacements;
	
	/**
	 * Constructor of a Event
	 * 
	 * @param source the source of the event
	 */
	public ShowPlacementEvent(Object source, int[] showPlacements) {
		super(source);
		this.showPlacements = showPlacements;
	}

	/**
	 * Returns the string representation of this event
	 * 
	 * @returns the string representation of this event
	 */
	public String toString() {
		return "(ShowPlacementEvent)[" + CommonRoutines.prettyPrint(showPlacements) + "]";
	}

	public int[] getShowPlacements() {
		return showPlacements;
	}
}

