package com.topcoder.client.spectatorApp.messages;

import java.io.Serializable;

/**
 * Defines the component contest connection (ie defines the connection
 * to retrieve information about the component contest)
 * @author Pops
 */
public class DefineComponentContestConnection implements Serializable {
	/** The contestID related to this component event */
	private int contestID;

	/** The roundID related to this component event */
	private int roundID;

	/** The componentID related to this component event */
	private long componentID;

	/** The url that will be used to query for this component */
	private String url;

	/** How often (in milliseconds) to poll the url */
	private long pollTime;


	public DefineComponentContestConnection(int contestID, int roundID, long componentID, String url, long pollTime) {
		this.contestID = contestID;
		this.roundID = roundID;
		this.componentID = componentID;
		this.url = url;
		this.pollTime = pollTime;
	}

	public long getComponentID() {
		return componentID;
	}

	public void setComponentID(long componentID) {
		this.componentID = componentID;
	}

	public int getContestID() {
		return contestID;
	}

	public void setContestID(int contestID) {
		this.contestID = contestID;
	}

	public int getRoundID() {
		return roundID;
	}

	public void setRoundID(int roundID) {
		this.roundID = roundID;
	}

	public long getPollTime() {
		return pollTime;
	}

	public void setPollTime(long pollTime) {
		this.pollTime = pollTime;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
