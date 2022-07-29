package com.appirio.commons.mq;

/**
 * Contains data for a message.
 * 
 * @author james
 */
public class Message {
    private MessageMetadata metadata;
    private String body;
    private String type;

    public Message() {
        
    }
    
    public Message(String body) {
    	this.body = body;
    }
    
    public Message(String body, MessageMetadata metadata) {
        this.body = body;
        this.metadata = metadata;
    }
    
    /**
     * Associated service metadata
     */
    public MessageMetadata getMetadata() {
        return metadata;
    }

    /**
     * Associated service metadata
     */
    public void setMetadata(MessageMetadata metadata) {
        this.metadata = metadata;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
    
    /**
     * A custom type field for the message
     */
	public String getType() {
		return type;
	}

    /**
     * A custom type field for the message
     */
	public void setType(String type) {
		this.type = type;
	}

}
