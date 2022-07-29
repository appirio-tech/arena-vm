/**
 * Description: Contains information to show a component
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
package com.topcoder.client.spectatorApp.event;

public class ShowComponentResultsEvent extends ComponentEvent {
	
	private int delay;
	
	/**
	 * Constructor of a Show Component Event
	 * 
	 * @param source the source of the event
	 * @param contestID the unique contest identifier
	 * @param roundID the unique round identifier
	 * @param delay the delay (in seconds) to show between them
	 */
	public ShowComponentResultsEvent(Object source, int contestID, int roundID, int delay) {
		super(source, contestID, roundID, -1);
		this.delay = delay;
	}

	public int getDelay() {
		return delay;
	}
}
