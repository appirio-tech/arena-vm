package com.topcoder.client.spectatorApp.announcer.events;

import com.topcoder.client.spectatorApp.messages.ShowComponentResultsByComponentID;

/**
 * The show component results for the given component id.  This event will implement
 * the javabean standard since it is read by the XMLDecoder
 * 
 * @author Pops
 */
public class ShowComponentResultsByComponentIDEvent extends AnnouncerEvent {
	/** The contest id to show */
	private int contestID;

	/** The round id to show */
	private int roundID;

	/** The delay in seconds */
	private int delay;

	/** Empty constructor - required by javabean standard */
	public ShowComponentResultsByComponentIDEvent() {}

	/** Returns the ShowRound message */
	public Object getMessage() {
		return new ShowComponentResultsByComponentID(contestID, roundID, delay);
	}

	/** Return the contest id */
	public int getContestID() {
		return contestID;
	}

	/** Sets the contest id */
	public void setContestID(int contestID) {
		this.contestID = contestID;
	}

	/** Return the round id */
	public int getRoundID() {
		return roundID;
	}

	/** Sets the round id */
	public void setRoundID(int roundID) {
		this.roundID = roundID;
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
	public void validateEvent() {
	}
}
