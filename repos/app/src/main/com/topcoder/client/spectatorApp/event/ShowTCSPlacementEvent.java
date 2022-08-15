/**
 * Description: Contains information about what placements to show
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
package com.topcoder.client.spectatorApp.event;

import com.topcoder.client.spectatorApp.CommonRoutines;

public class ShowTCSPlacementEvent extends java.util.EventObject {

	private int[] showPlacements;
        private int roundID;
	
	/**
	 * Constructor of a Event
	 * 
	 * @param source the source of the event
	 */
	public ShowTCSPlacementEvent(Object source, int[] showPlacements, int roundID) {
		super(source);
		this.showPlacements = showPlacements;
                this.roundID = roundID;
	}

	/**
	 * Returns the string representation of this event
	 * 
	 * @returns the string representation of this event
	 */
	public String toString() {
		return "(ShowTCSPlacementEvent)[" + CommonRoutines.prettyPrint(showPlacements) + "]";
	}

	public int[] getShowPlacements() {
		return showPlacements;
	}
        
        public int getRoundID() {
            return roundID;
        }
}

