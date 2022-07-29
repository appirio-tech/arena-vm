package com.topcoder.client.spectatorApp.announcer.events;

import com.topcoder.client.spectatorApp.messages.ShowSystemTestResultsByCoder;

/**
 * The show system test results for the given coder  
 * 
 * This event will implement the javabean standard since it is read by the XMLDecoder
 * 
 * @author Pops
 */
public class ShowSystemTestResultsByCoderEvent extends AnnouncerEvent {

	/** The round id to show */
	private int roundID;
	
	/** The coder id to show */
	private int coderID;
	
	/** The delay in seconds */
	private int delay;
	
	/** Empty constructor - required by javabean standard */
	public ShowSystemTestResultsByCoderEvent() {
	}

	/** Returns the ShowRound message */
	public Object getMessage() {
		return new ShowSystemTestResultsByCoder(roundID, coderID, delay);
	}	
	
	/** Return the round id */
	public int getRoundID() {
		return roundID;
	}

	/** Sets the round id */
	public void setRoundID(int roundID) {
		this.roundID = roundID;
	}
	
	/** Return the coder id */
	public int getCoderID() {
		return coderID;
	}

	/** Sets the coder id */
	public void setCoderID(int coderID) {
		this.coderID = coderID;
	}
	
	/** Return the delay */
	public int getDelay() {
		return delay;
	}

	/** Sets the delay */
	public void setDelay(int delay) {
		this.delay = delay;
	}
	
	/** Nothing to validate! */
	public void validateEvent() {}
}
