package com.topcoder.client.spectatorApp.announcer.events;

import com.topcoder.client.spectatorApp.messages.ShowComponent;

/**
 * The show component event. This event will implement the javabean standard since
 * it is read by the XMLDecoder
 * 
 * @author Pops
 */
public class ShowComponentEvent extends AnnouncerEvent {
	/** The contest to show */
	private int contestID;

	/** The round to show */
	private int roundID;

	/** The component id to show */
	private long componentID;

	/** Empty constructor - required by javabean standard */
	public ShowComponentEvent() {}

	/** Returns the ShowRound message */
	public Object getMessage() {
		return new ShowComponent(contestID, roundID, componentID);
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

	/** Return the component id */
	public long getComponentID() {
		return componentID;
	}

	/** Sets the round id */
	public void setComponentID(long componentID) {
		this.componentID = componentID;
	}

	/** Nothing to validate! */
	public void validateEvent() {}
}
