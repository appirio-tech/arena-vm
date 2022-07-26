package com.topcoder.client.spectatorApp.messages;

import java.io.Serializable;

/**
 * Information about a TCS Coder
 */
public class TCSCoderInfo implements Serializable {

	/** The handle */
	private String handle;
	
	/** The seed */
	private int seed;
	
	/** The TCRating */
	private int tcRating;
	
	public TCSCoderInfo() {}
	/**
	 * Construct the object
	 */
	public TCSCoderInfo(String handle, int seed, int tcRating) {
		super();
		this.handle = handle;
		this.seed = seed;
		this.tcRating = tcRating;
	}
	/**
	 * @return
	 */
	public String getHandle() {
		return handle;
	}

	/**
	 * @param handle
	 */
	public void setHandle(String handle) {
		this.handle = handle;
	}

	/**
	 * @return
	 */
	public int getSeed() {
		return seed;
	}

	/**
	 * @param seed
	 */
	public void setSeed(int seed) {
		this.seed = seed;
	}

	/**
	 * @return
	 */
	public int getTcRating() {
		return tcRating;
	}

	/**
	 * @param tcRating
	 */
	public void setTcRating(int tcRating) {
		this.tcRating = tcRating;
	}
	
	public String toString() {
		return new StringBuffer("(TCSCoderInfo)[").append(handle).append(", ").append(seed).append(", ").append(tcRating).append("]").toString();
	}

}
