/**
 * Description: Listener for an appeal change
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
package com.topcoder.client.spectatorApp.scoreboard.model;


public interface AppealChangeListener extends java.util.EventListener {
	/**
	 * Notification of an appeal update
	 * @param evt the appeal change event
	 */
	public void appealUpdated(AppealChangeEvent evt);

}
