package com.topcoder.server.replay;

import org.apache.log4j.Category;

import com.topcoder.server.common.*;
import com.topcoder.server.messaging.TopicMessageSubscriber;
import com.topcoder.server.processor.Processor;
import com.topcoder.server.services.EventService;
import com.topcoder.server.broadcaster.*;

import javax.jms.ObjectMessage;
import javax.jms.JMSException;
import java.util.LinkedList;
import java.util.Iterator;


public class ReplayReciever implements Runnable {

    private static Category trace = Category.getInstance(ReplayReciever.class.getName());
    private ExodusLocalClient m_toMIT = new ExodusLocalClient();
    private boolean m_active = true;

    public ReplayReciever() {
    }

    private boolean isActive() {
        return m_active;
    }

    public void deactivate() {
        m_active = false;
        m_toMIT.stop();
    }

    public final static int TIMEOUT = 10000;

    public void run() {
        trace.info("Starting replay reciever run");
        m_toMIT.start();

        while (isActive()) {
            try {
                // get an event to replay
                TCEvent event = (TCEvent) m_toMIT.receive();
                EventService.sendGlobalEvent(event);
            } catch (Throwable ex) {
                trace.error("Error reading message:", ex);
            }
        }
        trace.info("Finishing listener run");
    }

    public static void main(String[] args) {
        Thread t = new Thread(new ReplayReciever());
        t.start();
    }
}
