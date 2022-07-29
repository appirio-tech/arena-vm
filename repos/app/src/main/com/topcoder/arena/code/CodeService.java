package com.topcoder.arena.code;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.appirio.commons.mq.Message;
import com.appirio.commons.mq.MessageQueueService;
import com.appirio.commons.mq.monitor.MonitorListener;
import com.appirio.commons.mq.monitor.QueueMonitor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.topcoder.arena.exception.ArenaRuntimeException;
import com.topcoder.farm.controller.configuration.ApplicationContextProvider;
import com.topcoder.farm.controller.configuration.EnvironmentConfig;
import com.topcoder.farm.controller.exception.InvalidRequirementsException;
import com.topcoder.farm.controller.model.QueueConfig;
import com.topcoder.farm.controller.services.ControllerServices;
import com.topcoder.farm.processor.api.CodeProcessingRequest;
import com.topcoder.farm.processor.api.CodeProcessingRequestMetadata;
import com.topcoder.farm.processor.api.CodeProcessingRequestMetadata.LanguageType;
import com.topcoder.farm.processor.api.CodeProcessingResult;
import com.topcoder.server.util.StringCompressionUtil;

/**
 * Provides code-based services (compile, test, etc) for processing by an
 * external processor.
 * 
 * @author james
 */
@Service(value = "codeService")
public class CodeService {
	private static final Logger logger = Logger.getLogger(CodeService.class);

	private static final int MAX_RETRIEVE_MESSAGES = 10;
	// 255 KB (sqs max is 256 but we need buffer for attributes)
	private static final int MAX_MESSAGE_SIZE = 261120; 

	private MessageQueueService messageQueueService;
	private String queuePrefix;
	private ObjectMapper objectMapper;
	private ExecutorService executor;
	private Map<String, CodeProcessingResult> resultsMap;
	private QueueMonitor queueMonitor;
	private CodeResultQueueListener resultListener;
	private Map<String, String> queueNameMap;
	private ControllerServices controller;
	private String resultQueueName;
	private long syncCheckInterval;

	@Autowired
	public CodeService(MessageQueueService messageQueueService, EnvironmentConfig config, QueueMonitor monitor,
			ControllerServices controller, CodeServiceConfiguration svcConfig) {
		this.messageQueueService = messageQueueService;
		queuePrefix = config.getPrefix() + config.getAppServiceName() + '-';
		objectMapper = new ObjectMapper();
		this.executor = Executors.newCachedThreadPool();
		this.queueMonitor = monitor;
		this.controller = controller;
		syncCheckInterval = svcConfig.getSyncCheckInterval();

		queueNameMap = new HashMap<String, String>();
		for (QueueConfig qc : controller.getQueueConfigs()) {
			final String key = buildQueueKey(qc.getRound().toUpperCase(), qc.getApp().toUpperCase(), qc.getAction()
					.toUpperCase(), qc.isPractice(), qc.getPlatform().toUpperCase());
			queueNameMap.put(key, queuePrefix + qc.getQueueName());
		}
		logger.info("queue mapping: " + queueNameMap);

		String queue = svcConfig.getResultQueueName();
		if (!queue.isEmpty()) {
			resultQueueName = queuePrefix + queue;
			resultsMap = new ConcurrentHashMap<String, CodeProcessingResult>();
			resultListener = new CodeResultQueueListener();
			logger.info("Monitoring result queue " + resultQueueName);
			queueMonitor.monitor(svcConfig.getResultMonitorInterval(), svcConfig.getMaxMonitorMessages(), true, true,
					resultListener, resultQueueName);
		}

	}

	/**
	 * Returns the name of the queue that processor results should be sent to in
	 * order to be processed by this code service.
	 */
	public String getResultQueueName() {
		return resultQueueName;
	}

	/**
	 * Sends the request to a processor for processing.
	 * 
	 * @param request
	 *            The request to send to a processor for processing
	 * @return A future promise containing the result if the job has been set
	 *         for synchronous processing; otherwise, the return will be null
	 */
	public Future<CodeProcessingResult> sendToProcessor(final CodeProcessingRequest request) {
		final String requestQueueName = getRequestQueueName(request.getMetadata());

		if (request.getMetadata().isSynchronous()) {
			if (request.getMetadata().getSyncTimeout() == null) {
				throw new IllegalArgumentException("Synchronous call requires a timeout value");
			}

			// if we are going to wait for the results, make sure we
			// have an id set
			if (request.getMetadata().getRequestId() == null) {
				request.getMetadata().setRequestId(UUID.randomUUID().toString());
			}
		}

		// set the queue the processor needs to set the results into
		// so we can get them
		if (request.getMetadata().getResultsQueueName() == null) {
			request.getMetadata().setResultsQueueName(resultQueueName);
		}

		try {
			// serialize the request as json and send to the processing queue
			String json = objectMapper.writeValueAsString(request);
			Message msg;
			if (json.length() > MAX_MESSAGE_SIZE) {
				logger.warn(String.format("Message size too large(%d): %s", json.length(), json));
				// try to compress the json
				String compressed = StringCompressionUtil.compress(json);
				if (compressed.length() > MAX_MESSAGE_SIZE) {
					logger.warn("Compressed message size still too large: " + compressed.length());
					throw new IllegalArgumentException("Code request is too large");
				}
				logger.info("Compressed size = " + compressed.length());
				msg = new Message(compressed);
				msg.setType("gzip");
			} else {
				msg = new Message(json);
			}
			messageQueueService.sendMessages(requestQueueName, msg);

			logger.info("Message sent");
		} catch (Exception e) {
			throw new ArenaRuntimeException("Unable to execute request:" + e.getMessage(), e);
		}

		// only go into new thread in request is synchronous
		if (request.getMetadata().isSynchronous()) {
			// wait in a new thread
			return executor.submit(new Callable<CodeProcessingResult>() {
				@Override
				public CodeProcessingResult call() throws Exception {
					try {
						logger.debug("Starting wait for synchronous request...");
						// wait here for the listener to put the results in the results map
						// convert timeout sec to millis
						long timeout = 1000 * request.getMetadata().getSyncTimeout();
						long start = System.currentTimeMillis();
						do {
							CodeProcessingResult res = resultsMap.remove(request.getMetadata().getRequestId());
							if (res != null) {
								return res;
							} else {
								Thread.sleep(syncCheckInterval);
							}
						} while ((System.currentTimeMillis() - start) < timeout);

						logger.warn("Code request " + request.getMetadata().getRequestId() + " timed out.");

						// timeout or async
						return null;

					} catch (Exception e) {
						logger.error("Unable to submit code request: " + e.getMessage(), e);
						throw new InvalidRequirementsException("Unable to execute request:" + e.getMessage(), e);
					}
				}
			});
		} else {
			logger.debug("Request is asynchronous - not waiting for response.");
			return null;
		}
	}

	private String buildQueueKey(String round, String app, String action, boolean practice, String platform) {
		// format: (ROUND | ANY).APP.ACTION.PRACTICE.PLATFORM
		// e.g., ANY.SRM.COMPILE.false.NIX
		StringBuilder keyBuilder = new StringBuilder(80);
		keyBuilder.append(round).append('.');
		keyBuilder.append(app).append('.');
		keyBuilder.append(action).append('.');
		keyBuilder.append(Boolean.toString(practice)).append('.');
		keyBuilder.append(platform);
		return keyBuilder.toString();
	}

	private String getRequestQueueName(CodeProcessingRequestMetadata metadata) {
		String queueName = null;
		if (metadata.getAction() == null) {
			throw new IllegalArgumentException("No action type set");
		}

		if (metadata.getApp() == null) {
			throw new IllegalArgumentException("No action type set");
		}

		String round = metadata.getRoundId() != null ? metadata.getRoundId().toString() : "ANY";
		String platform;
		if (metadata.getLanguage() != null && metadata.getLanguage().equals(LanguageType.DOTNET)) {
			platform = "WINDOWS";
		} else {
			platform = "NIX";
		}
		String action = metadata.getAction().toString();
		String app = metadata.getApp().toString();
		String key = buildQueueKey(round, app, action, metadata.isPractice(), platform);

		queueName = queueNameMap.get(key);
		if (queueName == null) {
			if (metadata.getRoundId() != null) {
				// try again w/out round
				key = buildQueueKey("ANY", app, action, metadata.isPractice(), platform);
				queueName = queueNameMap.get(key);
			}
			if (queueName == null) {
				throw new IllegalArgumentException("Unable to find queue for " + key);
			}
		}
		return queueName;
	}

	private class CodeResultQueueListener implements MonitorListener {

		@Override
		public void messageReceived(Message message, QueueMonitor monitor) {
			try {
				boolean debug = logger.isDebugEnabled();
				if (debug) {
					logger.debug("Code result received: " + message.getBody());
				}
				CodeProcessingResult res = objectMapper.readValue(message.getBody(), CodeProcessingResult.class);
				if (res != null && res.getMetadata().getRequestId() != null) {
					// see if this is a synchronous call and if so, update the
					// results map that is actively being watched
					if (res.getMetadata().isSynchronous()) {
						if (debug) {
							logger.debug("Found sync result: " + res.getMetadata().getRequestId());
						}
						resultsMap.put(res.getMetadata().getRequestId(), res);
					}

					// if there is a handler, call the handler with the result
					if (res.getMetadata().getResultHandlerName() != null) {
						if (debug) {
							logger.debug("Calling handler " + res.getMetadata().getResultHandlerName());
						}
						handleResult(res);
					}

				} else {
					logger.warn("Null result returned");
				}
			} catch (Exception e) {
				logger.error("Unable to process received results message: " + e.getMessage(), e);
			} finally {
				messageQueueService.deleteMessage(message.getMetadata().getQueueName(), message.getMetadata());
			}
		}

		@SuppressWarnings("unchecked")
		private void handleResult(CodeProcessingResult res) {

			try {
				Class<CodeProcessingResultHandler> resultClass = (Class<CodeProcessingResultHandler>) Class.forName(res
						.getMetadata().getResultHandlerName());
				CodeProcessingResultHandler handler = ApplicationContextProvider.getContext().getBean(resultClass);
				handler.handleResult(res);
			} catch (Exception e) {
				logger.error("Unable to handle code processing result:" + e.getMessage(), e);
			}
		}
	}

	/**
	 * Makes an effort to delete any queued requests that have not yet started
	 * processing.
	 * 
	 * @param clientName
	 *            The old processor id
	 * @param metadata
	 *            Metadata used to determine which queue to delete from. The
	 *            request tag from the metadata is also need to match requests
	 *            to delete
	 */
	public void deletePendingCodeRequests(String clientName, CodeProcessingRequestMetadata metadata) {
		String prefixTag = metadata.getRequestTag();
		controller.cancelPendingRequests(clientName, prefixTag);

		String clientQueueName = getRequestQueueName(metadata);

		ObjectMapper om = new ObjectMapper();
		// retrieve messages from queue without peeking b/c we may need to
		// delete some
		List<Message> messages = messageQueueService.retrieveMessages(clientQueueName, MAX_RETRIEVE_MESSAGES, 0, false);

		if (messages != null && !messages.isEmpty()) {
			CodeProcessingRequest cpr;
			for (Message m : messages) {
				try {
					cpr = om.readValue(m.getBody(), CodeProcessingRequest.class);
					if (cpr.getMetadata().getRequestTag() != null
							&& prefixTag.equals(cpr.getMetadata().getRequestTag())) {
						// delete message from queue
						messageQueueService.deleteMessage(clientQueueName, m.getMetadata());
					} else {
						// not the message we are looking for - return to queue
						messageQueueService.returnMessageToQueue(clientQueueName, m.getMetadata());
					}
				} catch (Exception e) {
					logger.warn("An error occurred while reading the queue:" + e.getMessage(), e);
				}
			}
		}
	}
}
