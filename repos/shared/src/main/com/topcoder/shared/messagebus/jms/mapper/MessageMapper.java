/*
 * MessageMapper
 * 
 * Created 10/03/2007
 */
package com.topcoder.shared.messagebus.jms.mapper;

import java.io.IOException;
import java.util.Date;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import com.topcoder.shared.messagebus.BusMessage;
import com.topcoder.shared.util.logging.Logger;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public abstract class MessageMapper {
    private Logger log = Logger.getLogger(getClass());

    /**
     * 
     */
    private static final String BM_SERIALIZATION_METHOD = "BMSerializationMethod";
    /**
     * 
     */
    private static final String BM_BODY_TYPE = "BMBodyType";
    /**
     * 
     */
    private static final String BM_TYPE = "BMType";
    /**
     * 
     */
    private static final String BM_DATE = "BMDate";
    /**
     * 
     */
    private static final String BM_ORIGIN_MODULE = "BMOriginModule";
    /**
     * 
     */
    private static final String BM_ORIGIN_VM = "BMOriginVM";
    /**
     * 
     */
    private static final String BM_VERSION = "BMVersion";

    protected void fillMessage(BusMessage src, Message message) throws JMSException, IOException {
        message.setJMSCorrelationID(src.getMessageCorrelationId());
        message.setStringProperty(BM_VERSION, src.getMessageVersion());
        message.setStringProperty(BM_ORIGIN_VM, src.getMessageOriginVM());
        message.setStringProperty(BM_ORIGIN_MODULE, src.getMessageOriginModule());
        message.setLongProperty(BM_DATE, src.getMessageDate().getTime());
        message.setStringProperty(BM_TYPE, src.getMessageType());
        message.setStringProperty(BM_BODY_TYPE, src.getMessageBodyType());
        fillMessageBody(src, message);
    }
    


    protected void fillMessage(Message src, BusMessage message) throws JMSException, IOException {
        message.setMessageId(src.getJMSMessageID());
        message.setMessageCorrelationId(src.getJMSCorrelationID());
        message.setMessageVersion(src.getStringProperty(BM_VERSION));
        message.setMessageOriginVM(src.getStringProperty(BM_ORIGIN_VM));
        message.setMessageOriginModule(src.getStringProperty(BM_ORIGIN_MODULE));
        message.setMessageType(getMessageType(src));
        message.setMessageDate(new Date(src.getLongProperty(BM_DATE)));
        message.setMessageBodyType(getMessageBodyType(src));
        fillMessageBody(src, message);
    }

    public static String getMessageBodyType(Message src) throws JMSException {
        return src.getStringProperty(BM_BODY_TYPE);
    }

    public static String getMessageType(Message src) throws JMSException {
        return src.getStringProperty(BM_TYPE);
    }
    
    public static String getMessageBodySerializationMethod(Message src) throws JMSException {
        return src.getStringProperty(BM_SERIALIZATION_METHOD);
    }
    
    public static void setMessageSerializationMethod(Message message, String value) throws JMSException {
        message.setStringProperty(BM_SERIALIZATION_METHOD, value);
    }    
    
    protected BusMessage newMessage() {
        return new BusMessage();
    }
    
    public Message toJMSMessage(BusMessage src, Session session) throws JMSException, IOException {
        if (log.isDebugEnabled()) {
            log.debug("Mapping Bus message to JMS message");
        }
        Message message = newJMSMessage(src, session);
        fillMessage(src, message);
        if (log.isDebugEnabled()) {
            log.debug("Mapped message="+message);
        }
        return message;
    }
    
    public BusMessage fromJMSMessage(Message src) throws JMSException, IOException {
        if (log.isDebugEnabled()) {
            log.debug("Mapping JMS message to Bus message");
        }
        BusMessage message = newMessage();
        fillMessage(src,  message);
        if (log.isDebugEnabled()) {
            log.debug("Mapped message="+message);
        }
        return message;
    }

    protected abstract Message newJMSMessage(BusMessage src, Session session) throws JMSException;

    protected abstract void fillMessageBody(BusMessage src, Message message) throws JMSException, IOException;
    
    protected abstract void fillMessageBody(Message src, BusMessage message) throws JMSException, IOException;

}
