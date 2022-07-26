package com.appirio.commons.mq.monitor;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.appirio.commons.mq.Message;
import com.appirio.commons.mq.MessageQueueService;

@Service
@Scope("prototype")
public class QueueMonitor {

	private static final Logger logger = Logger.getLogger(QueueMonitor.class);

	private MessageQueueService messageQueueService;
	private boolean enabled = false;
	private ExecutorService listenerExecutor;
	private ExecutorService monitorExecutor;

	@Autowired
	public QueueMonitor(MessageQueueService messageQueueService) {
		this.messageQueueService = messageQueueService;
	}

	/**
	 * Continuously monitors one or more message queues and calls a listener
	 * when messages are received. Note that the listener is called in a new
	 * thread for each message so that the queue can continue to be processed.
	 * 
	 * @param monitorRate
	 *            The fixed rate in milliseconds to check for new messages
	 * @param maxMessages
	 *            The maximum number of messages to check for in a given queue
	 * @param useMonitorThread
	 *            Whether to use a separate monitor thread for monitoring.
	 * @param useListenerThreads
	 *            Whether to use separate listener threads when messages are
	 *            received and continue to monitor for incoming messages (true)
	 *            or to continue to monitor once listeners have all been called
	 *            (false).
	 * @param listener
	 *            A listener to be called when messages a received
	 * @param queueNames
	 *            One or more queues to monitor
	 */
	public void monitor(final long monitorRate, final int maxMessages, final boolean useMonitorThread,
			final boolean useListenerThreads, final MonitorListener listener, final String... queueNames) {
		enabled = true;

		if (useMonitorThread && monitorExecutor == null) {
			if (monitorExecutor == null) {
				monitorExecutor = Executors.newSingleThreadExecutor();
			}

			monitorExecutor.submit(new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					pollContinuously(monitorRate, maxMessages, useListenerThreads, listener, queueNames);
					return null;
				}
			});
			
			return;

		}

		pollContinuously(monitorRate, maxMessages, useListenerThreads, listener, queueNames);

		logger.info("Monitor disabled. No longer monitoring queues");
	}

	public void pollContinuously(long monitorRate, final int maxMessages, final boolean useListenerThreads,
			final MonitorListener listener, final String... queueNames) {
		while (enabled) {
			long start = System.currentTimeMillis();

			poll(maxMessages, useListenerThreads, listener, queueNames);

			try {
				long delay = monitorRate - (System.currentTimeMillis() - start);
				if (delay >= 0) {
					Thread.sleep(delay);
				}
			} catch (InterruptedException ie) {
				logger.error("Error during monitor delay: " + ie.getMessage());
			}
		}
	}

	/**
	 * Polls (once) one or more message queues and calls a listener when
	 * messages are received.
	 * 
	 * @param maxMessages
	 *            The maximum number of messages to check for in a given queue
	 * @param useListenerThreads
	 *            Whether to use separate listener threads when messages are
	 *            received and continue to monitor for incoming messages (true)
	 *            or to continue to monitor once listeners have all been called
	 *            (false).
	 * @param listener
	 *            A listener to be called when messages a received
	 * @param queueNames
	 *            One or more queues to monitor
	 */
	public void poll(int maxMessages, boolean useListenerThreads, MonitorListener listener, String... queueNames) {
		boolean debug = logger.isDebugEnabled();
		for (String queueName : queueNames) {
			if (debug) {
				logger.debug("Checking for messages in queue " + queueName);
			}
			try {
				List<Message> messages = messageQueueService.retrieveMessages(queueName, maxMessages, 0, false);
				if (messages != null && !messages.isEmpty()) {
					for (Message msg : messages) {
						if (enabled) {
							notifyListener(listener, useListenerThreads, msg);
						} else {
							logger.info("Monitor is no longer enabled. Returning message to the queue");

							// return message to the queue
							messageQueueService.returnMessageToQueue(queueName, msg.getMetadata());
						}
					}
				}
			} catch (Throwable t) {
				logger.error("An error occurred while processing messages: " + t.getMessage(), t);
			}
		}
	}

	private void notifyListener(MonitorListener listener, boolean useListenerThreads, Message message) {
		try {
			if (useListenerThreads) {
				if (listenerExecutor == null) {
					listenerExecutor = Executors.newCachedThreadPool();
				}
				listenerExecutor.submit(new ListenerRunner(listener, message, this));
			} else {
				listener.messageReceived(message, this);
			}
		} catch (Throwable t) {
			logger.error("Unable to submit listener task: " + t.getMessage(), t);
		}
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	private class ListenerRunner implements Runnable {

		private MonitorListener listener;
		private Message message;
		private QueueMonitor monitor;

		public ListenerRunner(MonitorListener listener, Message message, QueueMonitor monitor) {
			this.listener = listener;
			this.message = message;
			this.monitor = monitor;
		}

		@Override
		public void run() {
			try {
				listener.messageReceived(message, monitor);
			} catch (Throwable t) {
				// the listener should be catching exceptions but in case they
				// don't, we want to continue monitoring
				logger.error("An error occurred while calling the message listener:" + t.getMessage(), t);
			} finally {
				// remove references so objects are gc'd
				listener = null;
				message = null;
				monitor = null;
			}
		}

	}

}
