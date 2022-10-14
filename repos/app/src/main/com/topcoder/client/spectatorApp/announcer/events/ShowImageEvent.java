package com.topcoder.client.spectatorApp.announcer.events;

import com.topcoder.client.spectatorApp.messages.ShowImage;

/**
 * The show round event.  
 * 
 * This event will implement the javabean standard since it is read by the XMLDecoder
 * 
 * @author Pops
 */
public class ShowImageEvent extends AnnouncerEvent {

	/** The round id to show */
	private String path;
	
	/** Empty constructor - required by javabean standard */
	public ShowImageEvent() {
	}

	/** Returns the ShowRound message */
	public Object getMessage() {
		return new ShowImage(path);
	}	
	
	/** Return the round id */
	public String getImagePath() {
		return path;
	}

	/** Sets the round id */
	public void setImagePath(String path) {
		this.path = path;
	}
	
	/** Nothing to validate! */
	public void validateEvent() {}
}
