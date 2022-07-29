/*
 * Message
 * 
 * Created Oct 1, 2007
 */
package com.topcoder.shared.messagebus;

import java.util.Date;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class BusMessage {
    public static final String CURRENT_VERSION = "01.00";
    private String messageVersion = CURRENT_VERSION;
    /**
     * The VM ID of the process who put the message on the bus
     */
    private String messageOriginVM;
    /**
     * The Module name who put the message on the bus
     */
    private String messageOriginModule;
    /**
     * The Date of the message
     */
    private Date   messageDate;
    /**
     * The type of the message
     */
    private String messageType;
    /**
     * The body type of the message
     */
    private String messageBodyType;
    /**
     * The message body
     */
    private Object messageBody;
    /**
     * The id given to the message by the service
     */
    private String messageId;
    /**
     * The id of the message who this message is in response of
     */
    private String messageCorrelationId;

    public String getMessageVersion() {
        return messageVersion;
    }
    public String getMessageOriginVM() {
        return messageOriginVM;
    }
    public String getMessageOriginModule() {
        return messageOriginModule;
    }
    public String getMessageType() {
        return messageType;
    }
    public String getMessageBodyType() {
        return messageBodyType;
    }
    public Date getMessageDate() {
        return messageDate;
    }
    public void setMessageVersion(String messageVersion) {
        this.messageVersion = messageVersion;
    }
    public void setMessageOriginVM(String originVM) {
        this.messageOriginVM = originVM;
    }
    public void setMessageOriginModule(String generatorModule) {
        this.messageOriginModule = generatorModule;
    }
    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
    public void setMessageBodyType(String messageTypeID) {
        this.messageBodyType = messageTypeID;
    }
    public void setMessageDate(Date messageDate) {
        this.messageDate = messageDate;
    }
    public Object getMessageBody() {
        return messageBody;
    }
    public void setMessageBody(Object messageBody) {
        this.messageBody = messageBody;
    }
    public String getMessageId() {
        return messageId;
    }
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
    
    public String getMessageCorrelationId() {
        return messageCorrelationId;
    }
    public void setMessageCorrelationId(String messageCorrelationId) {
        this.messageCorrelationId = messageCorrelationId;
    }
    public String toString() {
        StringBuilder sb = new StringBuilder(100);
        sb.append(this.getClass().getSimpleName()).append("[")
            .append("messageVersion=").append(messageVersion).append(",")
            .append("messageOriginVM=").append(messageOriginVM).append(",")
            .append("messageOriginModule=").append(messageOriginModule).append(",")
            .append("messageType=").append(messageType).append(",")
            .append("messageBodyType=").append(messageBodyType).append(",")
            .append("messageDate=").append(messageDate).append(",")
            .append("messageBody=").append(messageBody == null ? null : messageBody.getClass().getName())
            .append("]");
        return sb.toString();
    }

}
