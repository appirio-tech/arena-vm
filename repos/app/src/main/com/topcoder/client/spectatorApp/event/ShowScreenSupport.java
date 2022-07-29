/**
 * Description: Event set support class for
 * ShowScreenListener. Manages listener registration and contains fire
 * functions.
 * 
 * @author visualage
 * @version 1.0
 */
package com.topcoder.client.spectatorApp.event;

import com.topcoder.client.spectatorApp.scoreboard.model.*;
import com.topcoder.client.spectatorApp.AbstractListenerSupport;
import com.topcoder.client.spectatorApp.event.ShowScreenEvent;
import com.topcoder.client.spectatorApp.event.ShowScreenListener;
import com.topcoder.client.spectatorApp.netClient.SpectatorEventProcessor;



/**
 * ScreenChangeSupport bottlenecks support for classes that fire events to
 * ScreenChangeListener listeners.
 */
public class ShowScreenSupport extends AbstractListenerSupport<ShowScreenListener> {
	/**
	 * Fires notifications off to all listeners (in reverse order)
	 * 
	 * @param evt
	 *           the event
	 */
	public synchronized void fireUpdateScreen(ShowScreenEvent evt) {
		// Fire the event to all listeners (done in reverse order from how they
		// were added).
		for (int i = listeners.size() - 1; i >= 0; i--) {
			listeners.get(i).showScreens(evt);
		}
	}
}
