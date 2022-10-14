/*
 * JMSProducer
 * 
 * Created Oct 3, 2007
 */
package com.topcoder.shared.messagebus.jms;

import java.io.IOException;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;

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
public class JMSProducer {
    private Logger log = Logger.getLogger(getClass());
    private Object sendMutex = new Object();
    private MessageMapperProvider mapperProvider;
    private Session session;
    private MessageProducer producer;
    private Destination destination;
    private boolean sharedConnection;
    private JMSConnection cnn;

    public JMSProducer(JMSConnection cnn, Session session, MessageProducer producer, Destination destination, boolean sharedConnection, MessageMapperProvider mapperProvider) {
        this.cnn = cnn;
        this.session = session;
        this.producer = producer;
        this.destination = destination;
        this.sharedConnection = sharedConnection;
        this.mapperProvider = mapperProvider;
        log.debug("Created "+this+" destination="+this.destination);
    }

    protected Message send(BusMessage message) throws JMSException, IOException, MapperNotFoundException, MapperProviderException {
        if (log.isDebugEnabled()) {
            log.debug("Sending : "+message);
        }
        Message jmsMsg = convertToJMS(message);
        cnn.assertConnected();
        synchronized (sendMutex) {
            producer.send(jmsMsg);
        }
        message.setMessageId(jmsMsg.getJMSMessageID());
        return jmsMsg;
    }

    private Message convertToJMS(BusMessage message) throws JMSException, IOException, MapperNotFoundException, MapperProviderException {
        MessageMapper mapper = mapperProvider.getMapper(message.getMessageType(), message.getMessageBodyType());
        Message convertedMessage = mapper.toJMSMessage(message, session);
        return convertedMessage;
    }

    protected void finalize() throws Throwable {
       bareClose();
    }

    public void close() {
        if (log.isDebugEnabled()) {
            log.debug("Closing JMS producer: "+this);
        }
        bareClose();
    }

    private void bareClose() {
        try { producer.close(); } catch (Exception e) { log.error(e, e); };
        try { session.close();  } catch (Exception e) { log.error(e, e); };
        if (!sharedConnection) {
            try { cnn.close();  } catch (Exception e) { log.error(e, e); };
        }
    }
    
    public String toString() {
        return getClass().getSimpleName()+"["+System.identityHashCode(this)+"]";
    }
}
