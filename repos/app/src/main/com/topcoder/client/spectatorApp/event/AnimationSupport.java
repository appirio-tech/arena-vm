/**
 *Event set support class for AnimationListener.
 * Manages listener registration and contains fire functions.
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
package com.topcoder.client.spectatorApp.event;

import com.topcoder.client.spectatorApp.AbstractListenerSupport;

/**
 * AnimationSupport bottlenecks support for classes that fire events to
 * AnimationListener listeners.
 */
public class AnimationSupport extends AbstractListenerSupport<AnimationListener> {
	/**
	 * Notifies all listeners that the timer was updated
	 * 
	 * @param event
	 *           the event to send to the listener
	 */
	public synchronized void fireAnimation(long now, long diff) {
		// Fire the event to all listeners (done in reverse order from how they
		// were added).
		for (int i = listeners.size() - 1; i >= 0; i--) {
			listeners.get(i).animate(now, diff);
		}		
	}
}
