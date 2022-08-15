/**
 * Description: Event set support class for
 * ShowPlacementListener. Manages listener registration and contains fire
 * functions.
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
package com.topcoder.client.spectatorApp.event;

import com.topcoder.client.spectatorApp.scoreboard.model.*;
import com.topcoder.client.spectatorApp.AbstractListenerSupport;
import com.topcoder.client.spectatorApp.PhaseTracker;
import com.topcoder.client.spectatorApp.event.PhaseEvent;
import com.topcoder.client.spectatorApp.event.PhaseListener;
import com.topcoder.client.spectatorApp.event.ShowPlacementEvent;
import com.topcoder.client.spectatorApp.event.ShowTCSPlacementEvent;
import com.topcoder.client.spectatorApp.event.ShowPlacementListener;
import com.topcoder.client.spectatorApp.netClient.SpectatorEventProcessor;



/**
 * PlacementChangeSupport bottlenecks support for classes that fire events to
 * PlacementChangeListener listeners.
 */
public class ShowPlacementSupport extends AbstractListenerSupport<ShowPlacementListener> {
	/**
	 * Fires notifications off to all listeners (in reverse order)
	 * 
	 * @param evt
	 *           the event
	 */
	public synchronized void fireUpdatePlacement(ShowPlacementEvent evt) {
		// Fire the event to all listeners (done in reverse order from how they
		// were added).
		for (int i = listeners.size() - 1; i >= 0; i--) {
			listeners.get(i).showPlacements(evt);
		}
	}
        
        public synchronized void fireUpdateTCSPlacement(ShowTCSPlacementEvent evt) {
		// Fire the event to all listeners (done in reverse order from how they
		// were added).
                    Round round = RoundManager.getInstance().getRound(evt.getRoundID());
                    if (round == null) {
                            return;
                    }
                    round.setTCSPlacements(evt.getShowPlacements());
	}
}
