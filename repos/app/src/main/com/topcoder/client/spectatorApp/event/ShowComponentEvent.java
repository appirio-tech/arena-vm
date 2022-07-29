/**
 * Description: Contains information to show a component
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
package com.topcoder.client.spectatorApp.event;

public class ShowComponentEvent extends ComponentEvent {
	/**
	 * Constructor of a Show Component Event
	 * 
	 * @param source the source of the event
	 * @param contestID the unique contest identifier
	 * @param roundID the unique round identifier
	 * @param componentID the unique component identifier
	 */
	public ShowComponentEvent(Object source, int contestID, int roundID, long componentID) {
		super(source, contestID, roundID, componentID);
	}
}
