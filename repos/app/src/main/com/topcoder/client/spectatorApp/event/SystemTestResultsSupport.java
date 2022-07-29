/**
 * SystemTestResultsSupport Description: Event set support class for System Test
 * Results Manages listener registration and contains fire functions.
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
package com.topcoder.client.spectatorApp.event;

import java.util.ArrayList;

/**
 * Bottlenecks support for classes that fire events to listeners.
 */
public class SystemTestResultsSupport {
	/** Holder for all listeners */
	private ArrayList systemTestResultsListeners = new ArrayList();

	/**
	 * Adds a listener
	 * 
	 * @param listener
	 *           the listener to be added
	 */
	public synchronized void addSystemTestResultsListener(SystemTestResultsListener listener) {
		// add a listener if it is not already registered
		if (!systemTestResultsListeners.contains(listener)) {
			systemTestResultsListeners.add(listener);
		}
	}

	/**
	 * Removes a listener
	 * 
	 * @param listener
	 *           the listener to be removed
	 */
	public synchronized void removeSystemTestResultsListener(SystemTestResultsListener listener) {
		// remove it if it is registered
		int pos = systemTestResultsListeners.indexOf(listener);
		if (pos >= 0) {
			systemTestResultsListeners.remove(pos);
		}
	}

	/**
	 * Notifies all listeners of a system test results for a given problem This
	 * method will notify in last-in-first-out (LIFO) order
	 * 
	 * @param event
	 *           the event to send to the listener
	 */
	public synchronized void fireSystemTestResultsByProblem(SystemTestResultsEvent event) {
		// Fire the event to all listeners (done in reverse order from how they
		// were added).
		for (int i = systemTestResultsListeners.size() - 1; i >= 0; i--) {
			SystemTestResultsListener listener = (SystemTestResultsListener) systemTestResultsListeners.get(i);
			listener.showProblemResults(event);
		}
	}

	/**
	 * Notifies all listeners of a system test results for a given coder This
	 * method will notify in last-in-first-out (LIFO) order
	 * 
	 * @param event
	 *           the event to send to the listener
	 */
	public synchronized void fireSystemTestResultsByCoder(SystemTestResultsEvent event) {
		// Fire the event to all listeners (done in reverse order from how they
		// were added).
		for (int i = systemTestResultsListeners.size() - 1; i >= 0; i--) {
			SystemTestResultsListener listener = (SystemTestResultsListener) systemTestResultsListeners.get(i);
			listener.showCoderResults(event);
		}
	}

	public synchronized void fireSystemTestResultsByCoderAll(SystemTestResultsEvent event) {
		for (int i = systemTestResultsListeners.size() - 1; i >= 0; i--) {
			SystemTestResultsListener listener = (SystemTestResultsListener) systemTestResultsListeners.get(i);
			listener.showCoderAllResults(event);
		}
	}
}
