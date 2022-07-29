package com.appirio.commons.mq;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.BatchResultErrorEntry;
import com.amazonaws.services.sqs.model.ChangeMessageVisibilityBatchRequestEntry;
import com.amazonaws.services.sqs.model.ChangeMessageVisibilityBatchResult;
import com.amazonaws.services.sqs.model.ChangeMessageVisibilityRequest;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.amazonaws.services.sqs.model.QueueDoesNotExistException;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.amazonaws.services.sqs.model.SendMessageBatchRequestEntry;
import com.amazonaws.services.sqs.model.SendMessageBatchResult;
import com.amazonaws.services.sqs.model.SendMessageBatchResultEntry;

/**
 * Amazon AWS SQS implementation of <code>MessageQueueService</code>
 * 
 * @author james
 */
@Service
public class SqsMessageQueueService implements MessageQueueService {

	private static final Logger logger = Logger.getLogger(SqsMessageQueueService.class);

	private final AmazonSQS sqsClient;

	private Map<String, String> queueUrlMap = new HashMap<String, String>();

	@Autowired
	public SqsMessageQueueService(@Qualifier("sqsClient") AmazonSQS sqs) {
		this.sqsClient = sqs;
	}

	@Override
	public boolean isQueueAvailable(String queueName) {
		boolean exists = queueUrlMap.containsKey(queueName);
		if (!exists) {
			try {
				sqsClient.getQueueUrl(queueName);
				exists = true;
			} catch (QueueDoesNotExistException qe) {
				logger.info("No queue found for " + queueName);
				return false;
			}
		}

		return exists;
	}

	private String getQueueUrl(String queueName) {
		String url = queueUrlMap.get(queueName);
		if (url == null) {
			logger.info("Getting URL for queue " + queueName);
			url = sqsClient.getQueueUrl(queueName).getQueueUrl();
			if (url != null) {
				queueUrlMap.put(queueName, url);
				logger.info("Using URL " + url + " for queue " + queueName);
			} else {
				throw new IllegalArgumentException("Invalid SQS queue name: " + queueName);
			}
		}
		return url;
	}

	@Override
	public void sendMessages(String queueName, Message... messages) {
		sendMessages(queueName, Arrays.asList(messages));
	}

	@Override
	public void sendMessages(String queueName, List<Message> messages) {
		String queueUrl = getQueueUrl(queueName);

		// according to docs, max batch size is 10
		int numSent = 0;
		while (numSent < messages.size()) {
			int endPos = (numSent + 10) > messages.size() ? messages.size() : numSent + 10;
			List<Message> subList = messages.subList(numSent, endPos);

			ArrayList<SendMessageBatchRequestEntry> msgList = new ArrayList<SendMessageBatchRequestEntry>(
					subList.size());

			for (int i = 0; i < subList.size(); i++) {
				final Message m = subList.get(i);
				// using index as a unique identifier within the batch
				final SendMessageBatchRequestEntry entry = new SendMessageBatchRequestEntry(Integer.toString(i), m.getBody());
				if (m.getType() != null) {
					entry.addMessageAttributesEntry("Type", new MessageAttributeValue().withDataType("String").withStringValue(m.getType()));
				}
				msgList.add(entry);
			}

			if (logger.isDebugEnabled()) {
				logger.debug("Sending " + messages.size() + " to " + queueName);
			}

			SendMessageBatchResult res = sqsClient.sendMessageBatch(queueUrl, msgList);
			if (res.getFailed() != null && !res.getFailed().isEmpty()) {
				logger.warn("Found failed messages: " + res.getFailed().toString());
				throw new IllegalStateException("Unable to send some messages: " + res.getFailed().toString());
			}

			if (res.getSuccessful() != null && !res.getSuccessful().isEmpty()) {
				for (SendMessageBatchResultEntry entry : res.getSuccessful()) {
					// update the original message with metadata
					int pos = Integer.parseInt(entry.getId()); // we set the id
																// = index
					// note: we have no receipt id as that is only given during
					// a retrieve operation
					subList.get(pos).setMetadata(new MessageMetadata(entry.getMessageId(), null, queueName));
				}
			} else {
				logger.error("Unexpected condition - no successful message information returned");
			}

			numSent += subList.size();
		}

	}

	@Override
	public List<Message> retrieveMessages(String queueName, Integer maxMessages, Integer waitTime, boolean peek) {
		ArrayList<Message> messages = new ArrayList<Message>(maxMessages);

		String url = getQueueUrl(queueName);
		boolean debug = logger.isDebugEnabled();
		if (debug) {
			logger.debug("Retrieving messages from " + url);
		}

		ReceiveMessageRequest req = new ReceiveMessageRequest(url);
		req.setMaxNumberOfMessages(maxMessages);
		req.setWaitTimeSeconds(waitTime);
		ReceiveMessageResult msgResult = sqsClient.receiveMessage(req);
		if (msgResult.getMessages() != null && !msgResult.getMessages().isEmpty()) {
			logger.info("Received " + msgResult.getMessages().size() + " messages from " + queueName);

			List<ChangeMessageVisibilityBatchRequestEntry> returnEntries = null;

			int i = 0;
			for (com.amazonaws.services.sqs.model.Message m : msgResult.getMessages()) {
				logger.info("Accepted " + queueName + " message with receipt " + m.getReceiptHandle() + " and id "
						+ m.getMessageId());

				final Message msg = new Message(m.getBody(), new MessageMetadata(m.getMessageId(), m.getReceiptHandle(),
						queueName));
				if (m.getMessageAttributes() != null && m.getMessageAttributes().containsKey("Type")) {
					msg.setType(m.getMessageAttributes().get("Type").getStringValue());
				}
				messages.add(msg);

				if (peek) {
					if (i++ == 0) {
						returnEntries = new ArrayList<ChangeMessageVisibilityBatchRequestEntry>(msgResult.getMessages()
								.size());
					}

					returnEntries.add(new ChangeMessageVisibilityBatchRequestEntry(Integer.toString(i), m
							.getReceiptHandle()).withVisibilityTimeout(0));
				}

				if (debug) {
					logger.debug("Read message " + m);
				}
			}

			if (peek) {
				logger.info("Return messages to queue...");
				ChangeMessageVisibilityBatchResult returnResult = sqsClient.changeMessageVisibilityBatch(url,
						returnEntries);
				if (returnResult.getFailed() != null && !returnResult.getFailed().isEmpty()) {
					logger.warn("Unable to return messages to queue during retrieval with peek=true");
					for (BatchResultErrorEntry err : returnResult.getFailed()) {
						logger.warn("Return message error:" + err.toString());
					}
				}
			}
		} else {
			if (debug) {
				logger.debug("No messages found");
			}
		}

		return messages;
	}

	private void changeVisibility(String queueName, MessageMetadata metadata, int visibilityTimeout) {
		String queueUrl = getQueueUrl(queueName);
		sqsClient.changeMessageVisibility(new ChangeMessageVisibilityRequest(queueUrl, metadata.getReceiptId(),
				visibilityTimeout));

		if (logger.isDebugEnabled()) {
			logger.debug("Changed visibility of " + metadata + " to " + visibilityTimeout);
		}
	}

	public void deleteMessage(String queueName, MessageMetadata metadata) {
		String queueUrl = getQueueUrl(queueName);

		sqsClient.deleteMessage(queueUrl, metadata.getReceiptId());

		logger.info(String.format("Deleted message %s from queue %s", metadata.getReceiptId(), queueUrl));

		// DeleteMessageBatchRequest req = new
		// DeleteMessageBatchRequest(queueUrl);
		//
		// ArrayList<DeleteMessageBatchRequestEntry> entries = new
		// ArrayList<DeleteMessageBatchRequestEntry>();
		// for (MessageMetadata mm : metadata) {
		// entries.add(new DeleteMessageBatchRequestEntry(mm.getMessageId(),
		// mm.getReceiptId()));
		// }
		// req.setEntries(entries);
		//
		// DeleteMessageBatchResult res = sqsClient.deleteMessageBatch(req);
		//
		// List<BatchResultErrorEntry> failed = res.getFailed();
		// if (failed != null && !failed.isEmpty()) {
		// for (BatchResultErrorEntry err : failed) {
		// logger.warn("Unable to delete message: " + err.toString());
		// }
		// throw new
		// IllegalStateException("Unable to delete one or more messages: " +
		// failed.toString());
		// }
		//
		// logger.info("Deleted " + entries.size() + " message(s)");
	}

	public void returnMessageToQueue(String queueName, MessageMetadata metadata) {
		changeVisibility(queueName, metadata, 0);

	}

	public void changeMessageTimeout(String queueName, int newTimeout, MessageMetadata metadata) {
		changeVisibility(queueName, metadata, newTimeout);
	}

}
