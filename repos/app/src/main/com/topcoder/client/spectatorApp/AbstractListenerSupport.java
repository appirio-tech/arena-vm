package com.topcoder.client.spectatorApp;

import java.util.ArrayList;
import java.util.EventListener;

/**
 * Base class to make writing listener supports easier 
 */
public abstract class AbstractListenerSupport<E extends EventListener> {
	
	/** Holder for all listeners */
	protected ArrayList<E> listeners = new ArrayList<E>();

	/**
	 * Adds a listener
	 * 
	 * @param listener
	 *           the listener to be added
	 */
	public synchronized void addListener(E listener) {
		// add a listener if it is not already registered
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	/**
	 * Removes a listener
	 * 
	 * @param listener
	 *           the listener to be removed
	 */
	public synchronized void removeListener(E listener) {
		listeners.remove(listener);
	}
}
