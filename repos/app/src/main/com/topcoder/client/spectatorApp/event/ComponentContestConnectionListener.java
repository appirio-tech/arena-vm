package com.topcoder.client.spectatorApp.event;

import java.util.EventListener;

/**
 * Defines the listener interface for component contest listeners
 * @author Pops
 */
public interface ComponentContestConnectionListener extends EventListener {
	public void defineConnection(int contestID, int roundID, long componentID, String url, long pollTime);
}
