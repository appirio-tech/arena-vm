package com.topcoder.client.spectatorApp.announcer.events;

import com.topcoder.client.spectatorApp.messages.ShowRound;

/**
 * The show round event.  
 * 
 * This event will implement the javabean standard since it is read by the XMLDecoder
 * 
 * @author Pops
 */
public class ShowRoundEvent extends AnnouncerEvent {

	/** The round id to show */
	private int roundID;
	
	/** Empty constructor - required by javabean standard */
	public ShowRoundEvent() {
	}

	/** Returns the ShowRound message */
	public Object getMessage() {
		return new ShowRound(roundID);
	}	
	
	/** Return the round id */
	public int getRoundID() {
		return roundID;
	}

	/** Sets the round id */
	public void setRoundID(int roundID) {
		this.roundID = roundID;
	}
	
	/** Nothing to validate! */
	public void validateEvent() {}
}
