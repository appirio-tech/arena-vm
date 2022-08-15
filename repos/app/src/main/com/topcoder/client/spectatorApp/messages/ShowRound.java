/**
 * ShowRound.java Description: Notifies the spectator application to show a
 * specific round information
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
package com.topcoder.client.spectatorApp.messages;

public class ShowRound implements java.io.Serializable {
	
	/** The round to show */
	private int roundID;

	/**
	 * No-arg constructor needed by customserialization
	 */
	public ShowRound() {}

	/** Default Constructor */
	public ShowRound(int roundID) {
		super();
		this.roundID = roundID;
	}

	/**
	 * Gets the round to whow
	 * 
	 * @returns the round information
	 */
	public int getRoundID() {
		return roundID;
	}

	public String toString() {
		return "(ShowRound)";
	}
}
