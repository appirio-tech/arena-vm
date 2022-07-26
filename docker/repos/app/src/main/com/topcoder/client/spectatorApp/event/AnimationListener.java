/**
 * Interface for animation updates
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
package com.topcoder.client.spectatorApp.event;

public interface AnimationListener extends java.util.EventListener {
	
	/**
	 * Method called on an animation update
	 * 
	 * @param now the current time
	 * @param diff the difference from the prior time
	 */
	public void animate(long now, long diff);
}
