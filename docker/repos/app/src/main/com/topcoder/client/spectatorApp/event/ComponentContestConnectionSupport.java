package com.topcoder.client.spectatorApp.event;

import com.topcoder.client.spectatorApp.AbstractListenerSupport;

/**
 * Support class for firing listener events
 * @author Pops
 */
public class ComponentContestConnectionSupport extends AbstractListenerSupport<ComponentContestConnectionListener> {
	public synchronized void fireDefineContest(int contestID, int roundID, long componentID, String url, long pollTime) {
		// Fire the event to all listeners (done in reverse order from how they
		// were added).
		for (int i = listeners.size() - 1; i >= 0; i--) {
			listeners.get(i).defineConnection(contestID, roundID, componentID, url, pollTime);
		}		
	}
}
