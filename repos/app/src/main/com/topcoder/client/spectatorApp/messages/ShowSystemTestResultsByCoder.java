/**
 * ShowRound.java
 *
 * Description:		Notifies the spectator application to show system test results for the coder
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.messages;


public class ShowSystemTestResultsByCoder implements java.io.Serializable {

    /** The round to show */
    private int roundID;

	/** The problem to show */
	private int coderID;

	/** The delay in seconds */
	private int delay;
	
    /**
     * No-arg constructor needed by customserialization
     *
     */
    public ShowSystemTestResultsByCoder() {
    }

    /** Default Constructor */
    public ShowSystemTestResultsByCoder(int roundID, int coderID, int delay) {
        super();
        this.roundID = roundID;
        this.coderID = coderID;
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
	 * Gets the coder to whow
	 *
	 * @returns the coderinformation
	 */
	public int getCoderID() {
		return coderID;
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
		 return new StringBuffer().append("(ShowSystemTestResultsByCoder)[").append(roundID).append(", ").append(coderID).append(", ").append(delay).append("]").toString();
	 }

}


