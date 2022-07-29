/**
 * Description: Notifies the spectator application to show component
 *  results for a component id
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
package com.topcoder.client.spectatorApp.messages;

public class ShowComponentResultsByComponentID implements java.io.Serializable {
	/** The contest to show */
	private int contestID;

	/** The round to show */
	private int roundID;

	/** The delay in seconds */
	private int delay;

	/**
	 * No-arg constructor needed by customserialization
	 */
	public ShowComponentResultsByComponentID() {}

	/** Default Constructor */
	public ShowComponentResultsByComponentID(int contestID, int roundID, int delay) {
		super();
		
		this.contestID = contestID;
		this.roundID = roundID;
		this.delay = delay;
	}

	/**
	 * Gets the contest to whow
	 * 
	 * @returns the contest information
	 */
	public int getContestID() {
		return contestID;
	}

	/**
	 * Gets the round to whow
	 * 
	 * @returns the round information
	 */
	public int getRoundID() {
		return roundID;
	}

	/**
	 * Gets the delay
	 * 
	 * @returns the delay
	 */
	public int getDelay() {
		return delay;
	}

	public String toString() {
		return new StringBuffer().append("(ShowComponentResultsByComponentID)[").append(contestID).append(", ").append(roundID).append(", ").append(delay).append("]").toString();
	}
}
