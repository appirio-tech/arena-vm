/**
 * PlacementChangeSupport.java Description: Event set support class for
 * PlacementChangeListener. Manages listener registration and contains fire
 * functions.
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
package com.topcoder.client.spectatorApp.scoreboard.model;

import java.util.ArrayList;
import com.topcoder.client.spectatorApp.AbstractListenerSupport;

/**
 * PlacementChangeSupport bottlenecks support for classes that fire events to
 * PlacementChangeListener listeners.
 */
public class PlacementChangeSupport extends AbstractListenerSupport<PlacementChangeListener> {
	/**
	 * Fires notifications off to all listeners (in reverse order)
	 * 
	 * @param evt
	 *           the event
	 */
	public synchronized void fireUpdatePlacement(PlacementChangeEvent evt) {
		// Fire the event to all listeners (done in reverse order from how they
		// were added).
		for (int i = listeners.size() - 1; i >= 0; i--) {
			listeners.get(i).placementChanged(evt);
		}
	}
}
