/*
 * ServiceEventMessageListener
 * 
 * Created Jul 17, 2008
 */
package com.topcoder.shared.serviceevent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import com.topcoder.shared.util.logging.Logger;

/**
 * @author Diego Belfer (Mural)
 * @version $Id$
 */
public class ServiceEventMessageListener implements MessageListener {

    private final Logger logger = Logger.getLogger(ServiceEventMessageListener.class);
    
    /**
     * Map containing listeners for eventType
     */
    private Map listeners = new HashMap();

    private String serviceName;
    
    public ServiceEventMessageListener(String serviceName) {
        this.serviceName = serviceName;
    }

    public void addListener(String eventType, ServiceEventHandler listener) {
        List typeListeners = null;
        synchronized (listeners) {
            typeListeners = (List) listeners.get(eventType);
            if (typeListeners == null) {
                typeListeners = new ArrayList();
                listeners.put(eventType, typeListeners);
            }
            typeListeners.add(listener);
        }
    }
    
    public void removeListener(ServiceEventHandler listener) {
        synchronized (listeners) {
            for (Iterator it = listeners.values().iterator(); it.hasNext();) {
                Set typeListeners= (Set) it.next();
                typeListeners.remove(listener);
            }
            listeners.remove(listener);
        }
    }
    
    protected void notifyListeners(String eventType, Serializable eventObject) {
        List allListeners = null;
        synchronized (listeners) {
            List typeListeners = (List) listeners.get(eventType);
            if (typeListeners == null) {
                allListeners = new ArrayList();
            } else {
                allListeners = new ArrayList(typeListeners);
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Incoming notification: "+serviceName+"#"+eventType);
        }
        for (Iterator it = allListeners.iterator(); it.hasNext();) {
            ServiceEventHandler listener = (ServiceEventHandler) it.next();
            try {
                listener.eventReceived(eventType, eventObject);
            } catch (RuntimeException e) {
                logger.error("ServiceEventHandler throw exception during notification.", e);
            }
        }
    }
    
    public void onMessage(Message message) {
        if (message != null && message instanceof ObjectMessage) {
            try {
                if (message.propertyExists("eventType")) {
                    notifyListeners(message.getStringProperty("eventType"), ((ObjectMessage) message).getObject());
                }
            } catch (Exception e) {
                logger.error("Invalid message received.", e);
            }
        }
    }

    public void clearListeners() {
        synchronized (listeners) {
            listeners.clear();
        }
    }

    public String getServiceName() {
        return serviceName;
    }
}
