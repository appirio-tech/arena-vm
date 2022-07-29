package com.topcoder.shared.serviceevent;

import java.io.Serializable;
import java.util.HashMap;

import com.topcoder.shared.common.ApplicationServer;
import com.topcoder.shared.messaging.TopicMessagePublisher;
import com.topcoder.shared.util.logging.Logger;

/**
 * @author Diego Belfer (Mural)
 * @version $Id$
 */
public class ServiceEventNotificator {

    private Logger logger = Logger.getLogger(ServiceEventNotificator.class);

    /**
     * Event notifications are sent through this publisher
     */
    private TopicMessagePublisher publisher;
    
    /**
     * Name of the service publishing events
     */
    private String serviceName;

    /**
     * Creates a new ServiceEventNotificator for the service name specified
     *  
     * @param topicName
     * @param serviceName
     * @throws IllegalStateException
     */
    public ServiceEventNotificator(String topicName, String serviceName) throws IllegalStateException {
        this.serviceName =  serviceName;
        try {
            logger.info("Initializing ServiceEventNotificator for topic=" + topicName + " service=" + serviceName);
            publisher = new TopicMessagePublisher(ApplicationServer.JMS_FACTORY, topicName);
            publisher.setPersistent(true);
            publisher.setFaultTolerant(false);
        } catch (Exception e) {
            throw (IllegalStateException) new IllegalStateException(
                    "Failed to initialize ServiceEventNotificator for topic=" + topicName + " service=" + serviceName)
                    .initCause(e);
        }
        logger.info("Initialized ServiceEventNotificator");
    }

    public void notifyEvent(String eventType, Serializable object) {
        if (logger.isDebugEnabled()) {
            logger.debug("Notifying "+serviceName+"#"+eventType);
        }
        HashMap props = new HashMap();
        props.put("serviceName", serviceName);
        props.put("eventType", eventType);
        publisher.pubMessage(props, object);
    }
    
    public void stop() {
        publisher.close();
    }
}
