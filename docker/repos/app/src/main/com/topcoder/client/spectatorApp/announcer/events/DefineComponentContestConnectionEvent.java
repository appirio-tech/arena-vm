package com.topcoder.client.spectatorApp.announcer.events;

import java.net.InetAddress;
import java.net.URI;
import com.topcoder.client.spectatorApp.messages.DefineComponentContestConnection;

/**
 * Defines the component contest connection event (ie defines the connection
 * to retrieve information about the component contest)
 * @author Pops
 */
public class DefineComponentContestConnectionEvent extends AnnouncerEvent {

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
	
	/** Public constructor required by serialization */
	public DefineComponentContestConnectionEvent() {}
	
	public Object getMessage() {
		return new DefineComponentContestConnection(contestID, roundID, componentID, url, pollTime);
	}

	public void validateEvent() throws Exception {
		if (url == null || url.length() == 0) {
			throw new IllegalArgumentException("Url must be specified");
		}
		
		final URI uri = URI.create(url);
		// Throws errors if not valid...
		if (uri.getHost() == null) {
			throw new IllegalArgumentException("The url host is malformed");
		} else {
			InetAddress.getByName(uri.getHost());
		}
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
