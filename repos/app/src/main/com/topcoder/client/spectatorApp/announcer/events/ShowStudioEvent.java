package com.topcoder.client.spectatorApp.announcer.events;



import com.topcoder.client.spectatorApp.messages.ShowStudio;


/**

 * Event to show the initial renderer.

 * 

 * This event will implement the javabean standard since it is read by the XMLDecoder

 * 

 * @author Pops

 */

public class ShowStudioEvent extends AbstractShowScreenEvent {



	/** Empty constructor as defined by the javabean standard */

	public ShowStudioEvent() {

	}

	/** Returns the ShowInitial message */

	public Object getMessage() {

		return new ShowStudio(getComputerNames(), getPath(), getHandles(), getTime(), getName());

	}
}

