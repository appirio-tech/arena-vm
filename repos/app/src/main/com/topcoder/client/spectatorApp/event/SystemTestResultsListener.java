/**
 * SystemTestResultsListener.java Description: Listener for system test result
 * events
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
package com.topcoder.client.spectatorApp.event;

public interface SystemTestResultsListener extends java.util.EventListener {
	/**
	 * Show the results for a given problem
	 * 
	 * @param evt
	 *           the System test results information event
	 */
	public abstract void showProblemResults(SystemTestResultsEvent evt);

	/**
	 * Show the results for a given coder
	 * 
	 * @param evt
	 *           the System test results information event
	 */
	public abstract void showCoderResults(SystemTestResultsEvent evt);

	/**
	 * Show the results of each coder (in placement order) 
	 * @param evt the system test results event
	 */
	public abstract void showCoderAllResults(SystemTestResultsEvent evt);
}
