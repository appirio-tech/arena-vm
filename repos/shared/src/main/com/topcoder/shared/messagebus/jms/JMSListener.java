/*
 * JMSListener
 * 
 * Created 10/03/2007
 */
package com.topcoder.shared.messagebus.jms;

import java.io.IOException;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;

import com.topcoder.shared.messagebus.BusException;
import com.topcoder.shared.messagebus.BusListener;
import com.topcoder.shared.messagebus.BusMessage;
import com.topcoder.shared.messagebus.jms.mapper.MapperNotFoundException;
import com.topcoder.shared.messagebus.jms.mapper.MapperProviderException;
import com.topcoder.shared.messagebus.jms.mapper.MessageMapper;
import com.topcoder.shared.messagebus.jms.mapper.MessageMapperProvider;
import com.topcoder.shared.util.logging.Logger;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class JMSListener implements BusListener {
    private Logger log = Logger.getLogger(getClass());
    private MessageMapperProvider mapperProvider;
    private Session session;
    private Destination destination;
    private boolean sharedConnection;
    private Connection cnn;
    private MessageConsumer consumer;
    private Handler handler;
    

    public JMSListener(Connection cnn, Session session, MessageConsumer consumer, Destination destination, boolean sharedConnection, MessageMapperProvider mapperProvider) throws JMSException {
        this.cnn = cnn;
        this.session = session;
        this.consumer = consumer;
        this.destination = destination;
        this.sharedConnection = sharedConnection;
        this.mapperProvider = mapperProvider;
        log.debug("Created "+this+" destination="+this.destination);
    }

    protected void handleIncomingMessage(Message msg) {
        try {
            if (log.isDebugEnabled()) {
                log.debug("JMS message Received: "+msg);
            }
            handler.handle(convertFromJMS(msg));
        } catch (Exception e) {
            log.error("Exception thrown while handling incoming message: "+msg, e);
        }
    }

    private BusMessage convertFromJMS(Message message) throws JMSException, IOException, MapperNotFoundException, MapperProviderException {
        String messageType = MessageMapper.getMessageType(message);
        String messageBodyType = MessageMapper.getMessageBodyType(message);
        String messageBodySerializationMethod = MessageMapper.getMessageBodySerializationMethod(message);
        MessageMapper mapper = mapperProvider.getMapper(messageType, messageBodyType, messageBodySerializationMethod);
        return mapper.fromJMSMessage(message);
    }

    protected void finalize() throws Throwable {
       bareClose();
    }

    public void close() {
        if (log.isDebugEnabled()) {
            log.debug("Closing JMS consumer: "+this);
        }
        bareClose();
    }

    private void bareClose() {
        try { consumer.close(); } catch (Exception e) { log.debug(e, e); };
        try { session.close();  } catch (Exception e) { log.debug(e, e); };
        if (!sharedConnection) {
            try { cnn.close();  } catch (Exception e) { log.debug(e, e); };
        }
    }
    
    public String toString() {
        return getClass().getSimpleName()+"["+System.identityHashCode(this)+"]";
    }

    public void setHandler(Handler handler) throws BusException {
        this.handler = handler;
        try {
            if (handler != null) {
                consumer.setMessageListener(new MessageListener() {
                    public void onMessage(Message msg) {
                        handleIncomingMessage(msg);
                    }
                });
            } else {
                consumer.setMessageListener(null);
            }
        } catch (JMSException e) {
           throw new BusException("Could not set the underlying message listener:",e);
        }
    }

    public void start() throws BusException {
        try {
            cnn.start();
        } catch (JMSException e) {
            throw new BusException("Exception while starting :"+this, e);
        }
    }

    public void stop() {
        close();
    }
}
