package com.topcoder.client.spectatorApp.announcer.events;



import com.topcoder.client.spectatorApp.messages.ShowNoHeaderScreen;


/**

 * Event to show the initial renderer.

 * 

 * This event will implement the javabean standard since it is read by the XMLDecoder

 * 

 * @author Pops

 */

public class ShowNoHeaderScreenEvent extends AbstractShowScreenEvent {



	/** Empty constructor as defined by the javabean standard */

	public ShowNoHeaderScreenEvent() {

	}

	/** Returns the ShowInitial message */

	public Object getMessage() {

		return new ShowNoHeaderScreen(getComputerNames(), getPath(), getHandles(), getTime(), getName());

	}
}

