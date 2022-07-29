/**
 * ShowRound.java
 *
 * Description:		Notifies the spectator application to show system test results for the problem
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.messages;


public class ShowSystemTestResultsByProblem implements java.io.Serializable {

    /** The round to show */
    private int roundID;

	/** The problem to show */
	private int problemID;

	/** The delay in seconds */
	private int delay;
	
    /**
     * No-arg constructor needed by customserialization
     *
     */
    public ShowSystemTestResultsByProblem() {
    }

    /** Default Constructor */
    public ShowSystemTestResultsByProblem(int roundID, int problemID, int delay) {
        super();
        this.roundID = roundID;
        this.problemID = problemID;
        this.delay = delay;
    }

    /**
     * Gets the round to whow
     *
     * @returns the round information
     */
    public int getRoundID() {
        return roundID;
    }

	/**
	 * Gets the problem to whow
	 *
	 * @returns the problem information
	 */
	public int getProblemID() {
		return problemID;
	}

	/**
	 * Gets the delay
	 *
	 * @returns the delay
	 */
	public int getDelay() {
		return delay;
	}

	
	public String toString() {
		 return new StringBuffer().append("(ShowSystemTestResultsByProblem)[").append(roundID).append(", ").append(problemID).append(", ").append(delay).append("]").toString();
	 }

}


