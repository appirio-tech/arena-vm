package com.topcoder.server.mpsqas.broadcast;

import com.topcoder.shared.util.logging.Logger;
import com.topcoder.shared.common.ApplicationServer;
import com.topcoder.shared.messaging.*;
import com.topcoder.server.common.*;
import com.topcoder.shared.util.DBMS;

import java.util.*;

/**
 * Class to user for publishing broadcasts to the mpsqasTopic.
 */
public class BroadcastPublisher {

    private static TopicMessagePublisher publisher;
    private static Logger logger = Logger.getLogger(BroadcastPublisher.class);
    private static int broadcastId = 0;

    /**
     * Initiatiates the TopicMessagePublisher.
     */
    static {
        logger.info("Initializing broadcast publisher.");
        try {
            publisher = new TopicMessagePublisher(ApplicationServer.JMS_FACTORY,
                    DBMS.MPSQAS_TOPIC);
            publisher.setPersistent(true);
            publisher.setFaultTolerant(false);
        } catch (Exception e) {
            logger.error("Error initializing broadcast publisher.", e);
        }
    }

    public static boolean broadcast(Broadcast broadcast) {
        broadcastId++;
        try {
            HashMap props = new HashMap();
            props.put("broadcastId", new Integer(broadcastId));
            logger.info("Broadcasting broadcastId=" + broadcastId);
            return publisher.pubMessage(props, broadcast);
        } catch (Exception e) {
            logger.error("Error publishing broadcast, broadcastId=" + broadcastId,
                    e);
            return false;
        }
    }
}
