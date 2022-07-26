/**
 * Description: Notifies the spectator application to show a
 * specific component
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
package com.topcoder.client.spectatorApp.messages;

public class ShowComponent implements java.io.Serializable {
	
	/** The contest to show */
	private int contestID;

	/** The round to show */
	private int roundID;

	/** The component to show */
	private long componentID;

	/**
	 * No-arg constructor needed by customserialization
	 */
	public ShowComponent() {}

	/** Default Constructor */
	public ShowComponent(int contestID, int roundID, long componentID) {
		super();
		this.contestID = contestID;
		this.roundID = roundID;
		this.componentID = componentID;
	}

	public int getContestID() {
		return contestID;
	}
	
	public int getRoundID() {
		return roundID;
	}
	
	/**
	 * Gets the component to show
	 * 
	 * @returns the component id
	 */
	public long getComponentID() {
		return componentID;
	}

	public String toString() {
		return "(ShowComponent) [" + componentID + "]";
	}
}
