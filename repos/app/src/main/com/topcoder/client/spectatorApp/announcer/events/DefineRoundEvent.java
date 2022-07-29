package com.topcoder.client.spectatorApp.announcer.events;

import com.topcoder.shared.netCommon.messages.spectator.DefineRound;

/**
 * The define round event.  
 * 
 * This event will implement the javabean standard since it is read by the XMLDecoder
 * 
 * @author Pops
 */
public class DefineRoundEvent extends AnnouncerEvent {

	/** Round ID */
	private int roundID;
	
	/** Round Type */
	private int roundType;
	
	/** Round Name */
	private String roundName;
	
	/** Contest ID */
	private int contestID;
	
	/** Empty constructor - required by java bean standard */
	public DefineRoundEvent() {
	}
	
	/** Return the DefineRound message */
	public Object getMessage() {
		return new DefineRound(roundID, roundType, roundName, contestID);
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

	/** Return the round name*/
	public String getRoundName() {
		return roundName;
	}

	/** Sets the round name */
	public void setRoundName(String roundName) {
		this.roundName = roundName;
	}

	/** Return the round type */
	public int getRoundType() {
		return roundType;
	}

	/** Sets the round type */
	public void setRoundType(int roundType) {
		this.roundType = roundType;
	}

	
	/** Nothing to validate! */
	public void validateEvent() {}
}
