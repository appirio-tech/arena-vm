package com.topcoder.server.mpsqas.listener;

import javax.jms.ObjectMessage;

import com.topcoder.server.mpsqas.broadcast.Broadcast;
import com.topcoder.shared.util.logging.Logger;
import com.topcoder.shared.common.ApplicationServer;
import com.topcoder.shared.messaging.TopicMessageSubscriber;
import com.topcoder.shared.util.DBMS;

/**
 * Listenes to the mpsqasTopic for broadcasts from the bean and sends the
 * broadcasts to the BroadcastProcessor
 *
 * @author mitalub
 */
public class BroadcastListener implements Runnable {

    private static final int TIMEOUT = 10000;

    private static final Logger logger = Logger.getLogger(
            BroadcastListener.class);
    private TopicMessageSubscriber tms;
    private Thread runner;
    private BroadcastProcessor broadcastProcessor;

    /**
     * Gets the connection to the mpsqas topic and starts the topic
     * listener thread.
     */
    public void init(BroadcastProcessor processor) {
        this.broadcastProcessor = processor;

        try {
            tms = new TopicMessageSubscriber(ApplicationServer.JMS_FACTORY,
                    DBMS.MPSQAS_TOPIC);
            tms.setFaultTolerant(false);
            logger.info("Got connection to MPSQAS_TOPIC.");

            runner = new Thread(this);
            runner.start();
            logger.info("Listener started.");
        } catch (Exception e) {
            logger.error("Exception starting broadcast listener.", e);
            logger.info("Broadcasts will not work until server is bounced.");
        }
    }

    /**
     * Constantly listens to the mpsqasTopic for broadcasts, and as the
     * broadcasts come in, dispatches them to the BroadcastProcessor
     */
    public void run() {
        ObjectMessage message;
        Broadcast broadcast;

        while (!runner.isInterrupted()) {
            message = null;
            broadcast = null;

            try {
                message = tms.getMessage(TIMEOUT);
                if (message != null) {
                    if (message.propertyExists("broadcastId")) {
                        logger.info("Broadcast with broadcastId="
                                + message.getIntProperty("broadcastId"));
                        broadcast = (Broadcast) message.getObject();
                        broadcastProcessor.processBroadcast(broadcast);
                    }
                }
            } catch (Exception e) {
                logger.error("Error reading message from topic.", e);
            }

        }
    }
}
