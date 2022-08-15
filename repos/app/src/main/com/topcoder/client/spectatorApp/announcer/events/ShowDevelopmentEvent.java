package com.topcoder.client.spectatorApp.announcer.events;



import com.topcoder.client.spectatorApp.messages.ShowDevelopment;


/**

 * Event to show the initial renderer.

 * 

 * This event will implement the javabean standard since it is read by the XMLDecoder

 * 

 * @author Pops

 */

public class ShowDevelopmentEvent extends AbstractShowScreenEvent {



	/** Empty constructor as defined by the javabean standard */

	public ShowDevelopmentEvent() {

	}

	/** Returns the ShowInitial message */

	public Object getMessage() {

		return new ShowDevelopment(getComputerNames(), getPath(), getHandles(), getTime(), getName());

	}
}

