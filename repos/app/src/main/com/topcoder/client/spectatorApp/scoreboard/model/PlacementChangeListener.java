/**
 * PlacementChangeListener.java Description: Listener for placement change events
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
package com.topcoder.client.spectatorApp.scoreboard.model;

public interface PlacementChangeListener extends java.util.EventListener {
	/**
	 * Signals that a placement change occurred.
	 * 
	 * @param evt the event
	 */
	public void placementChanged(PlacementChangeEvent evt);
}
