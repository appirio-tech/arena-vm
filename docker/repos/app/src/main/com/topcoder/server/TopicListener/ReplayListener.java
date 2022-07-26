package com.topcoder.server.TopicListener;

//import org.apache.log4j.Category;

import com.topcoder.server.common.*;
import com.topcoder.shared.common.ApplicationServer;
import com.topcoder.shared.util.DBMS;
import com.topcoder.server.messaging.TopicMessageSubscriber;
//import com.topcoder.server.processor.Processor;
//import com.topcoder.server.services.EventService;
import com.topcoder.server.broadcaster.*;
import com.topcoder.shared.util.logging.Logger;

import javax.jms.ObjectMessage;
import javax.jms.JMSException;
import java.util.LinkedList;
import java.util.Iterator;


public class ReplayListener implements Runnable {

    private static Logger trace = Logger.getLogger(ReplayListener.class);
    private TopicMessageSubscriber m_subscriber;
    private MITLocalClient m_toExodus = new MITLocalClient();
    private boolean m_active = true;

    public ReplayListener() {
        initialize();
    }

    public void initialize() {
        trace.info("initializing");
        boolean success = false;
        while (!success) {
            try {
                m_subscriber = new TopicMessageSubscriber(ApplicationServer.JMS_FACTORY, DBMS.EVENT_TOPIC);
                m_subscriber.setFaultTolerant(false);
                //m_subscriber.close();

                m_toExodus.start();
                success = true;
            } catch (Exception e) {
                trace.error("Failed to initialize.  Trying again:", e);
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ie) {
            }
        }
    }

    private boolean isActive() {
        return m_active;
    }

    public void deactivate() {
        m_active = false;
        if (m_subscriber != null) {
            trace.info("ETL: Closing TopicMessageSubscriber.");
            m_subscriber.deactivate();
            m_subscriber.close();
            m_subscriber = null;
        }
        m_toExodus.stop();
    }

    public final static int TIMEOUT = 10000;

    public void run() {
        trace.info("Starting listener run");
        while (isActive()) {
            try {
                ObjectMessage message = m_subscriber.getMessage(TIMEOUT);
                if (message != null && message.propertyExists(TCEvent.TYPE)) {
                    int type = message.getIntProperty(TCEvent.TYPE);
                    if (type == TCEvent.LIST_TYPE) {
                        LinkedList list = (LinkedList) message.getObject();
                        for (Iterator i = list.iterator(); i.hasNext();) {
                            TCEvent event = (TCEvent) i.next();
                            event.setReplayEvent(true);
                            trace.info("Sent: " + event.getEventType());
                            m_toExodus.send(event);
                        }
                    } else {
                        TCEvent event = (TCEvent) message.getObject();
                        trace.info("Sent: " + event.getEventType());
                        event.setReplayEvent(true);
                        m_toExodus.send(event);
                    }
                }

            } catch (JMSException e) {
                trace.error("Error reading message:", e);
                initialize();	 // Attempt to re-initialize the subscriber.
            } catch (Throwable ex) {
                trace.error("Error reading message:", ex);
            }
        }
        trace.info("Finishing listener run");
    }
}
