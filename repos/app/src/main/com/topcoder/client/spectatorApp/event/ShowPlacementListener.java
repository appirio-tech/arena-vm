/**
 * Description: Listener for placement change events
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
package com.topcoder.client.spectatorApp.event;

public interface ShowPlacementListener extends java.util.EventListener {
	public void showPlacements(ShowPlacementEvent evt);
        public void showTCSPlacements(ShowTCSPlacementEvent evt);
}
