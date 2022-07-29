package com.appirio.commons.mq;

import java.util.List;

/**
 * Interface for a message queue service.
 * 
 * @author james
 */
public interface MessageQueueService {

	/**
	 * Returns whether a given queue name exists.
	 * 
	 * @param queueName
	 *            The queue to check
	 * @return whether the queue is available
	 */
	public boolean isQueueAvailable(String queueName);
	
    /**
     * Sends messages to a message queue.
     * 
     * @param queueName
     *            The name of the queue to send messages to.
     * @param messages
     *            The messages to send. After sending, the messages will be updated with a <code>MessageMetadata</code> object.
     */
    public void sendMessages(String queueName, List<Message> messages);
    
    public void sendMessages(String queueName, Message... messages);

    /**
     * Retrieves messages from a message queue.
     * 
     * @param queueName
     *            The name of the queue to get messages from.
     * @param maxMessages
     *            The maximum number of messages to retrieve.
     * @param waitTime
     *            The maximum number of seconds to wait for messages.
     * @param peek
     *            Whether to read the messages and immediately return them to the queue. This is useful looking for analyzing (but not
     *            processing) messages in a queue.
     * @return The list of messages
     */
    public List<Message> retrieveMessages(String queueName, Integer maxMessages, Integer waitTime, boolean peek);

    /**
     * Deletes messages from a queue
     * 
     * @param queueName
     *            The name of the queue
     * @param metadata
     *            Metadata associated with existing queue message
     */
    public void deleteMessage(String queueName, MessageMetadata metadata);

    /**
     * Returns a message to the queue so they can be retrieved again.
     * 
     * @param queueName
     *            The name of the queue
     * @param metadata
     *            The metadata of the messages to return
     */
    public void returnMessageToQueue(String queueName, MessageMetadata metadata);

    /**
     * Changes the amount of time to process a message before they are returned to the queue. This is usually need if the message processor
     * needs to extend the amount of time to process a message.
     * 
     * @param queueName
     *            The name of the queue
     * @param metadata
     *            The metadata of the message
     */
    public void changeMessageTimeout(String queueName, int newTimeout, MessageMetadata metadata);
}
