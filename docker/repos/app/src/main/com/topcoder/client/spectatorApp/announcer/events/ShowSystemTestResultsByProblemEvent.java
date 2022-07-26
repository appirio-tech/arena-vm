package com.topcoder.client.spectatorApp.announcer.events;

import com.topcoder.client.spectatorApp.messages.ShowSystemTestResultsByProblem;

/**
 * The show system test results for the given problem  
 * 
 * This event will implement the javabean standard since it is read by the XMLDecoder
 * 
 * @author Pops
 */
public class ShowSystemTestResultsByProblemEvent extends AnnouncerEvent {

	/** The round id to show */
	private int roundID;
	
	/** The problem id to show */
	private int problemID;
	
	/** The delay in seconds */
	private int delay;
	
	/** Empty constructor - required by javabean standard */
	public ShowSystemTestResultsByProblemEvent() {
	}

	/** Returns the ShowRound message */
	public Object getMessage() {
		return new ShowSystemTestResultsByProblem(roundID, problemID, delay);
	}	
	
	/** Return the round id */
	public int getRoundID() {
		return roundID;
	}

	/** Sets the round id */
	public void setRoundID(int roundID) {
		this.roundID = roundID;
	}
	
	/** Return the problem id */
	public int getProblemID() {
		return problemID;
	}

	/** Sets the problem id */
	public void setProblemID(int problemID) {
		this.problemID = problemID;
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
