/**
 * Description: Contains information pertaining to a specific
 * component
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
package com.topcoder.client.spectatorApp.event;

public class ComponentEvent extends java.util.EventObject {
	
	/** Identifier of the contest */
	private int contestID;
	
	/** Identifier of the round */
	private int roundID;
	
	/** Identifier of the component */
	private long componentID;

	/**
	 * Constructor of a Componet Event
	 * 
	 * @param source the source of the event
	 * @param contestID the unique identifier of a contest
	 * @param roundID the unique identifier of a round
	 * @param componentID the unique identifier of a component
	 * @see com.topcoder.netCommon.contest.ContestConstants
	 */
	public ComponentEvent(Object source, int contestID, int roundID, long componentID) {
		super(source);
		this.contestID = contestID;
		this.roundID = roundID;
		this.componentID = componentID;
	}


	/** Returns the contest id */
	public int getContestID() {
		return contestID;
	}

	/** Returns the round id */
	public int getRoundID() {
		return roundID;
	}

	/** Returns the component id */
	public long getComponetID() {
		return componentID;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof ComponentEvent)) return false;
		final ComponentEvent e = (ComponentEvent) obj;
		return e.getContestID() == contestID && e.getRoundID() == roundID && e.getComponetID() == componentID;
	}
}
