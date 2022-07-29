package com.appirio.commons.mq.monitor;

import com.appirio.commons.mq.Message;

/**
 * A listener for queue monitor events.
 * 
 * @author james
 * 
 */
public interface MonitorListener {

	/**
	 * Called when a message is received.
	 * 
	 * @param message
	 *            The received message
	 * @param monitor
	 *            The invoking monitor
	 */
	public void messageReceived(Message message, QueueMonitor monitor);
}
