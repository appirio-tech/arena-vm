/**
 * Description: Event set support class for AppealChangeListeners.
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
package com.topcoder.client.spectatorApp.scoreboard.model;

import com.topcoder.client.spectatorApp.AbstractListenerSupport;

public class AppealChangeSupport extends AbstractListenerSupport<AppealChangeListener> implements java.io.Serializable {

	/**
	 * Notifies all listeners that an appeal status was updated.  This method will notify
	 * in last-in-first-out (LIFO) order
	 * 
	 * @param event the event to send to the listener
	 */
	public synchronized void fireProblemOpened(AppealChangeEvent event) {
		// Fire the event to all listeners (done in reverse order from how they
		// were added).
		for (int i = listeners.size() - 1; i >= 0; i--) {
			listeners.get(i).appealUpdated(event);
		}
	}
}
