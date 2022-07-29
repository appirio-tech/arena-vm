package com.topcoder.client.spectatorApp.announcer.events;



import com.topcoder.client.spectatorApp.messages.ShowDesign;


/**

 * Event to show the initial renderer.

 * 

 * This event will implement the javabean standard since it is read by the XMLDecoder

 * 

 * @author Pops

 */

public class ShowDesignEvent extends AbstractShowScreenEvent {



	/** Empty constructor as defined by the javabean standard */

	public ShowDesignEvent() {

	}

	/** Returns the ShowInitial message */

	public Object getMessage() {

		return new ShowDesign(getComputerNames(), getPath(), getHandles(), getTime(), getName());

	}
}

