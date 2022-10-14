/*
 * JMSPublisher
 * 
 * Created Oct 1, 2007
 */
package com.topcoder.shared.messagebus.jms;


import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;

import com.topcoder.shared.messagebus.BusException;
import com.topcoder.shared.messagebus.BusMessage;
import com.topcoder.shared.messagebus.BusPublisher;
import com.topcoder.shared.messagebus.jms.mapper.MessageMapperProvider;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class JMSPublisher extends JMSProducer implements BusPublisher {
    
    public JMSPublisher(JMSConnection cnn, Session session, MessageProducer producer, Destination destination, boolean sharedConnection, MessageMapperProvider mapperProvider) {
        super(cnn, session, producer, destination, sharedConnection, mapperProvider);
    }
    
    public void publish(BusMessage message) throws BusException {
        try {
            send(message);
        } catch (Exception e) {
            throw new BusException("Could not publish message: ", e);
        }
    }
}
