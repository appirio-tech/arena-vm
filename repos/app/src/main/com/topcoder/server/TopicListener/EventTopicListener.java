package com.topcoder.server.TopicListener;

//import org.apache.log4j.Category;

import java.util.Iterator;
import java.util.LinkedList;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import com.topcoder.server.common.TCEvent;
import com.topcoder.server.messaging.TopicMessageSubscriber;
import com.topcoder.server.processor.Processor;
import com.topcoder.server.services.EventService;
import com.topcoder.shared.util.logging.Logger;
import com.topcoder.shared.common.ApplicationServer;
import com.topcoder.shared.util.DBMS;


public final class EventTopicListener implements Runnable {

    private static final Logger trace = Logger.getLogger(EventTopicListener.class);
    private TopicMessageSubscriber m_subscriber;
    private boolean m_active = true;

    public EventTopicListener() {
        initialize();
    }

    private void initialize() {
        trace.info("initializing");
        boolean success = false;
        while (!success) {
            try {
                m_subscriber = new TopicMessageSubscriber(ApplicationServer.JMS_FACTORY, DBMS.EVENT_TOPIC);
                m_subscriber.setFaultTolerant(false);
                //m_subscriber.close();
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
    }

    private final static int TIMEOUT = 10000;

    public void run() {
        trace.info("Starting listener run");
        while (isActive()) {
            try {
                ObjectMessage message = m_subscriber.getMessage(TIMEOUT);
                if (message != null && message.propertyExists(TCEvent.TYPE)) {
                    int serverID = EventService.getServerID() + 1;
                    if (message.propertyExists(EventService.EVENT_KEY)) {
                        serverID = message.getIntProperty(EventService.EVENT_KEY);
                    }
                    if (serverID != EventService.getServerID()) {
                        int type = message.getIntProperty(TCEvent.TYPE);
                        if (type == TCEvent.LIST_TYPE) {
                            LinkedList list = (LinkedList) message.getObject();
                            for (Iterator i = list.iterator(); i.hasNext();) {
                                Processor.dispatchEvent((TCEvent) i.next());
                            }
                            Processor.flushPendingEvents();
                        } else {
                            Processor.dispatchEvent((TCEvent) message.getObject());
                            Processor.flushPendingEvents();
                        }
                    } else {
                        trace.debug("Ignoring message because server id differs");
                    }
                } else {
                    trace.debug("Ignoring message cause is null or without property TCEvent.TYPE");
                }

            } catch (JMSException e) {
                trace.error("Error reading message:", e);
                initialize();  // Attempt to re-initialize the subscriber.
            } catch (Throwable ex) {
                trace.error("Error reading message:", ex);
            }
        }
        trace.info("Finishing listener run");
    }
}
