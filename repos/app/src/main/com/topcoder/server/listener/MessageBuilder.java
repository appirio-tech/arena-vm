/*
 * MessageBuilder
 * 
 * Created 03/23/2006
 */
package com.topcoder.server.listener;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.topcoder.netCommon.contestantMessages.response.EndSyncResponse;
import com.topcoder.netCommon.contestantMessages.response.StartSyncResponse;

/**
 * This class builds the actual message objects that will be sent between the client and the server 
 * Inserts all necessary control messages to list of messages added by the user of this class.
 * 
 * 
 * @author Diego Belfer (mural)
 * @version $Id: MessageBuilder.java 44257 2006-04-18 14:40:19Z thefaxman $
 */
public class MessageBuilder {
    private List messages = new ArrayList();
    
    /**
     * Creates a new MessageBuilder with a initial capacity of the
     */
    public MessageBuilder() {
        messages = new ArrayList();
    }

    /**
     * Creates a new MessageBuilder with the specified initial capacity.
     *  
     * @param initialCapacity the initial capacity of the builder.
     */
    public MessageBuilder(int initialCapacity) {
        messages = new ArrayList(initialCapacity);
    }
    
    /**
     * Adds a message in the secuence of messages that are going to be included in 
     * the message builded by this MessageBuilder
     * 
     * @param message The message to be addded
     */
    public void add(Object message) {
        messages.add(message);
    }
    
    /**
     * Build the response message(s) with all messages added to this Builder. Besides, it adds
     * necessary control messages to allow the client to unblock from the synchronous request. 
     *  
     * @param requestId Id of the synchronous request
     *  
     * @return The message(s) that the server must send to the client
     */
    public MultiMessage buildResponseToSyncRequest(int requestId) {
        LinkedList allMessages = new LinkedList();
        allMessages.add(new StartSyncResponse(requestId));
        allMessages.add(resolveInternalMessages());
        allMessages.add(new EndSyncResponse(requestId));
        return new MultiMessage(allMessages);
    }

    /**
     * Build the response message(s) with all messages added to this Builder.  
     *  
     * @return The message(s) that the server must send to the client
     */
    public Object buildResponse() {
        return resolveInternalMessages();
    }

    /**
     * Return the message(s) that were added by the client
     * 
     * @return Either the only added message or the list of messages added
     */
    private Object resolveInternalMessages() {
        if (messages.size() == 1) {
            return messages.get(0);
        } else {
            return messages;
        }
    }
}
