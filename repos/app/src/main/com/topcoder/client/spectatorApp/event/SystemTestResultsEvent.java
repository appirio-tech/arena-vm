/**
 * RoomWinnerEvent.java
 *
 * Description:		Contains information pertaining to who won the room
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.event;


public class SystemTestResultsEvent extends java.util.EventObject {
	/** The identifier of the round the team is associated with */
	private int roundID;

	/** The id (problem or coder) associated with the event */
	private int resultsID;

	/** The delay (in seconds) */
	private int delay;


    /**
     *  Constructor of a Room Event
     *
     *  @param source the source of the event
     *  @param roundID the unique identifier of a round
     *  @param resultsID the results id (either problem or coder)
     *  @param delay the delay in seconds
     *
     */
    public SystemTestResultsEvent(Object source, int roundID, int resultsID, int delay) {
        super(source);
        this.roundID = roundID;
        this.resultsID = resultsID;
        this.delay = delay;
    }

	/**
	 * Returns the roundID.
	 * @return int
	 */
	public int getRoundID() {
		return roundID;
	}

	/**
	 * Returns the resultsID.
	 * @return int
	 */
	public int getResultsID() {
		return resultsID;
	}


	/**
	 * Returns the delay
	 * @return int
	 */
	public int getDelay() {
		return delay;
	}

}
