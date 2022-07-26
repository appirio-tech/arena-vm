package com.topcoder.server.TopicListener;

//import org.apache.log4j.Category;

import com.topcoder.server.common.*;
import com.topcoder.shared.common.ApplicationServer;
import com.topcoder.shared.util.DBMS;
import com.topcoder.server.messaging.TopicMessageSubscriber;
//import com.topcoder.server.processor.Processor;
import com.topcoder.server.services.*;
import com.topcoder.shared.util.logging.Logger;

import javax.jms.ObjectMessage;
import javax.jms.JMSException;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.ArrayList;


public class ChatArchiveListener implements Runnable {

    private static Logger trace = Logger.getLogger(ChatArchiveListener.class);

    public static final int WAIT_TIME = 5000;

    // this puppy actually will save to the db
    class DBWorker implements Runnable {

        public void run() {
            ArrayList curEvents = null;
            while (m_active) {
                try {
                    // copy and clear
                    while (m_chatEvents.isEmpty()) {
                        try {
                            Thread.sleep(WAIT_TIME);
                        } catch (InterruptedException ie) {
                        }
                    }
                    synchronized (m_chatEvents) {
                        curEvents = (ArrayList) m_chatEvents.clone();
                        m_chatEvents.clear();
                    }

                    trace.info("Saving: " + curEvents.size());

                    try {
                        //	 archive
                        CoreServices.archiveChat(curEvents);
                    } catch (Throwable t) {
                        trace.error("Failed to archiveChats", t);
                    }

                    //	 archive
                    //CoreServices.archiveChat(curEvents);

                    Thread.sleep(5000);
                } catch (Exception e) {
                    trace.info("Exception in Chat.DBWorker", e);
                }
            }
        }

    }

    private TopicMessageSubscriber m_subscriber;
    private boolean m_active = true;
    private Thread m_worker = new Thread(new DBWorker(), "ChatArchiveDBThread");

    public ChatArchiveListener() {
        initialize();
    }

    public void initialize() {
        trace.info("initializing");
        boolean initialized = false;
        while (!initialized) {
            try {
                m_subscriber = new TopicMessageSubscriber(ApplicationServer.JMS_FACTORY, DBMS.EVENT_TOPIC);
                m_subscriber.setFaultTolerant(false);
                //m_subscriber.close();
                initialized = true;
            } catch (Exception e) {
                trace.fatal("Failed to initialize", e);
                try {
                    Thread.sleep(WAIT_TIME);
                } catch (InterruptedException ie) {
                }
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

    public final static int TIMEOUT = 10000;

    ArrayList m_chatEvents = new ArrayList(1000);

    public void archiveEvent(TCEvent event) {
        if (event instanceof ChatEvent) {
            ChatEvent ce = (ChatEvent) event;
            if (ce.getUserMessage()) {
                synchronized (m_chatEvents) {
                    m_chatEvents.add(event);
                    m_chatEvents.notifyAll();
                }
            }
        }
    }

    public void run() {
        m_worker.setDaemon(true);
        m_worker.start();
        trace.info("Starting listener run");
        while (isActive()) {
            try {
                ObjectMessage message = m_subscriber.getMessage(TIMEOUT);
                trace.debug("Got message");
                if (message != null && message.propertyExists(TCEvent.TYPE)) {
                    int type = message.getIntProperty(TCEvent.TYPE);
                    if (type == TCEvent.LIST_TYPE) {
                        LinkedList list = (LinkedList) message.getObject();
                        synchronized (m_chatEvents) {
                            for (Iterator i = list.iterator(); i.hasNext();) {
                                TCEvent event = (TCEvent) i.next();
                                if (event instanceof ChatEvent) {
                                    int t = event.getEventType();
                                    if (t == TCEvent.ROOM_TARGET || t == TCEvent.USER_TARGET)
                                        m_chatEvents.add(event);
                                }
                            }
                        }
                    } else {
                        synchronized (m_chatEvents) {
                            TCEvent event = (TCEvent) message.getObject();
                            if (event instanceof ChatEvent) {
                                int t = event.getEventType();
                                if (t == TCEvent.ROOM_TARGET || t == TCEvent.USER_TARGET)
                                    m_chatEvents.add(event);
                            }
                        }
                    }
                }
            } catch (JMSException e) {
                trace.error("Error reading message:", e);
                initialize();	 // Attempt to re-initialize the subscriber.
            } catch (Exception ex) {
                trace.error("Error reading message:", ex);
            }
        }
        trace.info("Finishing listener run");
    }

    public static void main(String[] args) {
        Thread t = new Thread(new ChatArchiveListener());
        t.start();
    }
}
