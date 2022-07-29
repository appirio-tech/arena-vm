/**
 * Animation Manager supports listeners who want to be notified when
 * an animation stage is called
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
package com.topcoder.client.spectatorApp;

import com.topcoder.client.spectatorApp.event.AnimationListener;
import com.topcoder.client.spectatorApp.event.AnimationSupport;

public class AnimationManager {
	/** Support class for AnimationListeners */
	private AnimationSupport animationSpt = new AnimationSupport();

	/** The singleton instance of the heartbeat timer */
	private static AnimationManager instance;

	/**
	 * AnimationManager Constructor. The animation manager implements a singleton pattern.
	 * Please use AnimationManager.getInstance() to get an instance of it.
	 */
	private AnimationManager() {
	}

	/**
	 * Returns the singleton instance of the animation manager
	 * 
	 * @return AnimationManager
	 */
	public static synchronized AnimationManager getInstance() {
		if (instance == null) instance = new AnimationManager();
		return instance;
	}

	/**
	 * Fires animation updates to all listeners
	 * @param now the current time
	 * @param diff the difference from the prior time
	 */
	public void animate(long now, long diff)
	{
		animationSpt.fireAnimation(now, diff);
	}
	
	/**
	 * Adds a listener of type AnimationListener
	 * 
	 * @param listener the listener to add
	 */
	public synchronized void addAnimationListener(AnimationListener listener) {
		animationSpt.addListener(listener);
	}

	/**
	 * Removes a listener of type AnimationListener
	 * 
	 * @param listener the listener to remove
	 */
	public synchronized void removeAnimationListener(AnimationListener listener) {
		animationSpt.removeListener(listener);
	}
}
