package com.topcoder.client.spectatorApp.announcer.events;

import com.topcoder.client.spectatorApp.messages.ShowInitial;

/**
 * Event to show the initial renderer.
 * 
 * This event will implement the javabean standard since it is read by the XMLDecoder
 * 
 * @author Pops
 */
public class ShowInitialEvent extends AnnouncerEvent {

	/** Empty constructor as defined by the javabean standard */
	public ShowInitialEvent() {
	}
	
	/** Returns the ShowInitial message */
	public Object getMessage() {
		return new ShowInitial();
	}
	
	/** Nothing to validate! */
	public void validateEvent() {}
	
}
