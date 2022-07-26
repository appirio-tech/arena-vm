package com.appirio.commons.mq;

/**
 * Contains implementation metadata for a message.
 * 
 * @author james
 */
public class MessageMetadata {
	private String messageId;
	private String receiptId;
	private String queueName;

	public MessageMetadata() {

	}

	public MessageMetadata(String messageId, String receiptId, String queueName) {
		this.messageId = messageId;
		this.receiptId = receiptId;
		this.queueName = queueName;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getReceiptId() {
		return receiptId;
	}

	public void setReceiptId(String receiptId) {
		this.receiptId = receiptId;
	}

	public String getQueueName() {
		return queueName;
	}

	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

}
