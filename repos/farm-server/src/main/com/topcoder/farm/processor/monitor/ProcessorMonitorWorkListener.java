package com.topcoder.farm.processor.monitor;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.appirio.commons.mq.Message;
import com.appirio.commons.mq.MessageQueueService;
import com.appirio.commons.mq.monitor.MonitorListener;
import com.appirio.commons.mq.monitor.QueueMonitor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.topcoder.farm.processor.ProcessorService;
import com.topcoder.farm.processor.api.CodeProcessingRequest;
import com.topcoder.farm.processor.api.CodeProcessingResult;
import com.topcoder.server.util.StringCompressionUtil;

@Component
@Scope("prototype")
public class ProcessorMonitorWorkListener implements MonitorListener {

	private static final Logger logger = Logger.getLogger(ProcessorMonitorWorkListener.class);

	private MessageQueueService messageQueueService;
	private ObjectMapper objectMapper;
	private ProcessorService processorService;
	private boolean deleteMessageOnError = true;
	private int defaultTimeout = 5;

	@Autowired
	public ProcessorMonitorWorkListener(MessageQueueService messageQueueService, ProcessorService processorService) {
		this.messageQueueService = messageQueueService;
		this.processorService = processorService;
		objectMapper = new ObjectMapper();
	}
	
	/**
	 * Sets the default timeout.
	 * 
	 * @param defaultTimeout
	 *            The default timeout in minutes
	 */
	public void setDefaultTimeout(int defaultTimeout) {
		this.defaultTimeout = defaultTimeout;
	}

	@Override
	public void messageReceived(Message message, QueueMonitor monitor) {
		CodeProcessingRequest cpr = null;
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Received message " + message.getBody());
			}
			
			String body = message.getBody();
			if (message.getType() != null && message.getType().equals("gzip")) {
				logger.debug("Message body is compressed");
				body = StringCompressionUtil.decompress(body);
				if (logger.isDebugEnabled()) {
					logger.debug("Decompressed body: " + body);
				}
			}

			cpr = objectMapper.readValue(body, CodeProcessingRequest.class);

			int timeout = cpr.getMetadata().getProcessorTimeout() == null ? defaultTimeout : cpr.getMetadata()
					.getProcessorTimeout();
			if (logger.isDebugEnabled()) {
				logger.debug("Using timeout " + timeout);
			}
			CodeProcessingResult result = processorService.process(cpr, timeout, TimeUnit.MINUTES);

			String resultJson = objectMapper.writeValueAsString(result);

			// write message to results queue
			messageQueueService.sendMessages(cpr.getMetadata().getResultsQueueName(), new Message(resultJson));

			// delete the request message
			messageQueueService.deleteMessage(message.getMetadata().getQueueName(), message.getMetadata());
		} catch (Exception e) {
			logger.error(
					"An error occurred processing the code request:" + e.getMessage() + ";message=" + message.getBody(),
					e);
			if (deleteMessageOnError) {
				logger.info("Deleting message due to error");
				// need to send response back w/error information
				if (cpr != null) {
					try {
						CodeProcessingResult errResult = new CodeProcessingResult(cpr.getMetadata(), e.getMessage(),
								ExceptionUtils.getStackTrace(e));

						String json = objectMapper.writeValueAsString(errResult);

						messageQueueService.sendMessages(cpr.getMetadata().getResultsQueueName(), new Message(json));

						logger.info("Error response sent");

					} catch (Exception e2) {
						logger.error("Unable to send error response message: " + e2.getMessage(), e);
					}
				}

				messageQueueService.deleteMessage(message.getMetadata().getQueueName(), message.getMetadata());
			}
		}

	}
}
