/**
 * Description: Listener for screen change events
 * 
 * @author visualage
 * @version 1.0
 */
package com.topcoder.client.spectatorApp.event;

public interface ShowScreenListener extends java.util.EventListener {
	public void showScreens(ShowScreenEvent evt);
}
