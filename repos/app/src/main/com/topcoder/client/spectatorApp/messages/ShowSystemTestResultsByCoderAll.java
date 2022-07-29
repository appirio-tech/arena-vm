package com.topcoder.client.spectatorApp.messages;


public class ShowSystemTestResultsByCoderAll implements java.io.Serializable {

    /** The round to show */
    private int roundID;

	/** The delay in seconds */
	private int delay;
	
    /**
     * No-arg constructor needed by customserialization
     *
     */
    public ShowSystemTestResultsByCoderAll() {
    }

    /** Default Constructor */
    public ShowSystemTestResultsByCoderAll(int roundID, int delay) {
        super();
        this.roundID = roundID;
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
        /*
         *
	 * Gets the delay
	 *
	 * @returns the delay
	 */
	public int getDelay() {
		return delay;
	}
	
	public String toString() {
		 return new StringBuffer().append("(ShowSystemTestResultsByCoderAll)[").append(roundID).append(", ").append(delay).append("]").toString();
	 }

}


