/*
 * XStreamMapper
 * 
 * Created Oct 9, 2007
 */
package com.topcoder.shared.messagebus.jms.mapper.xstream;

import java.io.IOException;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import com.thoughtworks.xstream.XStream;
import com.topcoder.shared.messagebus.BusMessage;
import com.topcoder.shared.messagebus.jms.mapper.MessageMapper;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class XStreamMapper extends MessageMapper {
    private static final String METHOD = "XML:XStream";
    private static final XStream xs; 
    
    static {
        xs = new XStream();
        xs.setMode(XStream.XPATH_RELATIVE_REFERENCES);
    }
    
    protected void fillMessageBody(BusMessage src, Message message) throws JMSException, IOException {
        ((TextMessage) message).setText(xs.toXML(src.getMessageBody()));
        setMessageSerializationMethod(message, METHOD);
    }

    protected void fillMessageBody(Message src, BusMessage message) throws JMSException, IOException {
        message.setMessageBody(xs.fromXML( ((TextMessage) src).getText()));
    }

    protected TextMessage newJMSMessage(BusMessage src, Session session) throws JMSException {
        return session.createTextMessage();
    }

}
