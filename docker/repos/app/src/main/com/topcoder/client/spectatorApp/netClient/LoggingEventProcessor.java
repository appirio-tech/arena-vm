/**
 * LoggingEventProcessor.java Description: Class used to simply log messages
 * that are being processed
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
package com.topcoder.client.spectatorApp.netClient;

import org.apache.log4j.Category;

public class LoggingEventProcessor implements EventProcessor {
	/** reference to the logging category */
	private static final Category cat = Category.getInstance(LoggingEventProcessor.class.getName());

	/**
	 * Log the passed event
	 * 
	 * @param event the event to log
	 */
	public void processEvent(Object event) {
		cat.debug(event);
	}
}
