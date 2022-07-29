package com.topcoder.client.spectatorApp.announcer.events;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.messages.spectator.PhaseChange;

/**
 * The show round event. This event will implement the javabean standard since
 * it is read by the XMLDecoder
 * 
 * @author Pops
 */
public class ShowPhaseChangeEvent extends AnnouncerEvent {
	/** The phase id to show */
	private int phaseID;

	/** The time */
	private int timeAllocated;
	
	/** Empty constructor - required by javabean standard */
	public ShowPhaseChangeEvent() {}

	/** Returns the ShowRound message */
	public Object getMessage() {
		return new PhaseChange(phaseID, timeAllocated);
	}

	/** Return the phase id */
	public int getPhaseID() {
		return phaseID;
	}

	/** Sets the phase id */
	public void setPhaseID(int phaseID) {
		this.phaseID = phaseID;
	}

	public int getTimeAllocated() {
		return timeAllocated;
	}
	
	public void setTimeAllocated(int timeAllocated) {
		this.timeAllocated = timeAllocated;
	}
	
	/** Validate the phase id */
	public void validateEvent() throws Exception {
		boolean found = false;
		for(int x = 0; x < ContestConstants.SPECTATOR_PHASES.length; x++) {
			if (phaseID == ContestConstants.SPECTATOR_PHASES[x]) {
				found = true;
				break;
			}
		}
		
		if (!found) {
			throw new Exception("The phaseID is not valid (see ContestConstants for valid ids)");
		}
		
		if (timeAllocated < 0) {
			throw new Exception("The time allocated cannot be below 0");
		}
	}
}
