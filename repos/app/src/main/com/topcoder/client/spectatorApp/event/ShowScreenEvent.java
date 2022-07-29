/**
 * Description: Contains information about which screenshot to show.
 * 
 * @author visualage
 * @version 1.0
 */
package com.topcoder.client.spectatorApp.event;

import com.topcoder.client.spectatorApp.CommonRoutines;

public class ShowScreenEvent extends java.util.EventObject {

	private int[] showScreens;
	
	/**
	 * Constructor of a Event
	 * 
	 * @param source the source of the event
	 */
	public ShowScreenEvent(Object source, int[] showScreens) {
		super(source);
		this.showScreens = showScreens;
	}

	/**
	 * Returns the string representation of this event
	 * 
	 * @returns the string representation of this event
	 */
	public String toString() {
		return "(ShowScreenEvent)[" +  CommonRoutines.prettyPrint(showScreens) + "]";
	}

	public int[] getShowScreens() {
		return showScreens;
	}
}

