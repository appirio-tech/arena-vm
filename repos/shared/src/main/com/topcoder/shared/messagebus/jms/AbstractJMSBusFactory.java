/*
 * AbstractJMSBusFactory
 * 
 * Created Oct 8, 2007
 */
package com.topcoder.shared.messagebus.jms;

import java.util.HashMap;
import java.util.Map;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;

import com.topcoder.shared.messagebus.BusException;
import com.topcoder.shared.messagebus.BusFactory;
import com.topcoder.shared.messagebus.BusFactoryException;
import com.topcoder.shared.messagebus.BusListener;
import com.topcoder.shared.messagebus.BusPollListener;
import com.topcoder.shared.messagebus.BusPublisher;
import com.topcoder.shared.messagebus.BusRequestListener;
import com.topcoder.shared.messagebus.BusRequestListenerImpl;
import com.topcoder.shared.messagebus.BusRequestPublisher;
import com.topcoder.shared.messagebus.BusRequestPublisherImpl;
import com.topcoder.shared.messagebus.jms.mapper.MessageMapperProvider;

/**
 * Base class for Bus factory implementation using JMS as an underlying provider
 * for the Bus services.
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public abstract class AbstractJMSBusFactory extends BusFactory {
    private Map<String, ConnectionFactory> factories = new HashMap<String, ConnectionFactory>();
    private Map<String, JMSConnection> connections = new HashMap<String, JMSConnection>();
    private Map<String, Object> connectionsLock = new HashMap<String, Object>();
    private MessageMapperProvider mapperProvider;
    private JMSBusConfiguration configuration;
    

    public AbstractJMSBusFactory(JMSBusConfiguration configuration) {
        this.configuration = configuration;
        this.mapperProvider = new MessageMapperProvider(configuration.getMappers());
    }

    protected abstract String getFactoryKey(JMSChannelConfiguration cfg);
    protected abstract JMSConnection newConnection(JMSChannelConfiguration cfg) throws JMSException;
    protected abstract ConnectionFactory newFactory(JMSChannelConfiguration cfg);
    
    
    private JMSConnection getConnection(JMSChannelConfiguration cfg) throws BusFactoryException {
        try {
            if (cfg.isSharedConnection()) {
                return getSharedConnection(cfg);
            } else {
                 return newConnection(cfg);
            }
        } catch (Exception e) {
            throw new BusFactoryException("Could not create JMS connection for Configuration="+cfg, e);
        }
    }
    
    
    private JMSConnection getSharedConnection(JMSChannelConfiguration cfg) throws JMSException {
        JMSConnection cnn = getConnection(cfg.getSharedConnectionName());
        if (cnn != null) {
            return cnn;
        }
        Object lock = getLockForSharedConnection(cfg);
        synchronized (lock) {
            cnn = getConnection(cfg.getSharedConnectionName());
            if (cnn != null) {
                return cnn;
            }
            cnn = newConnection(cfg);
            //We need to start the connection before putting it in the pool
            cnn.start();
            putConnection(cfg.getSharedConnectionName(), cnn);
            return cnn;
        }
    }

    private void putConnection(String sharedConnectionName, JMSConnection cnn) {
        synchronized (connections) {
            connections.put(sharedConnectionName, cnn);
        }
    }

    private JMSConnection getConnection(String sharedConnectionName) {
        synchronized (connections) {
            return connections.get(sharedConnectionName);
        }
    }

    private Object getLockForSharedConnection(JMSChannelConfiguration cfg) {
        synchronized (connectionsLock) {
            Object lock = connectionsLock.get(cfg.getSharedConnectionName());
            if (lock == null) {
                lock = new Object();
                connectionsLock.put(cfg.getSharedConnectionName(), lock);
            }
            return lock;
        }
    }

    protected ConnectionFactory getConnectionFactory(JMSChannelConfiguration cfg) {
        String factoryKey = getFactoryKey(cfg);
        ConnectionFactory factory;
        synchronized (factories) {
            factory = factories.get(factoryKey);
        }
        if (factory == null) {
            factory = newFactory(cfg);
            synchronized (factories) {
                factories.put(factoryKey, factory);
            }
        }
        return factory;
    }

    
    public JMSListener createMessageListenerImpl(JMSChannelConfiguration cfg, boolean listener) throws BusFactoryException {
        try {
            JMSConnection cnn = getConnection(cfg);
            Session session = cnn.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination dest;
            if (JMSChannelConfiguration.TOPIC.equals(cfg.getDestinationType())) {
                dest = session.createTopic(cfg.getDestinationName());
            } else if (JMSChannelConfiguration.QUEUE.equals(cfg.getDestinationType())) {
                dest = session.createQueue(cfg.getDestinationName());
            } else {
                throw new BusFactoryException("Invalid configuration. Invalid JMS Destination type="+cfg.getDestinationType());
            }
            MessageConsumer consumer;
            if (cfg.isDurableSubscriber()) {
                consumer = session.createDurableSubscriber((Topic) dest, cfg.getDurableSubscriberName(), cfg.getSelectorString(), cfg.isNoLocal());
            } else {
                consumer = session.createConsumer(dest, cfg.getSelectorString(), cfg.isNoLocal());
            }
            if (listener) {
                return new JMSListener(cnn, session, consumer, dest, cfg.isSharedConnection(), mapperProvider);
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new BusFactoryException("Could not create JMS Consumer", e);
        }
    }

    public JMSProducer createMessageProducerImpl(JMSChannelConfiguration cfg, boolean publisher) throws BusFactoryException {
        try {
            JMSConnection cnn = getConnection(cfg);
            Session session = cnn.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination dest;
            if (JMSChannelConfiguration.TOPIC.equals(cfg.getDestinationType())) {
                dest = session.createTopic(cfg.getDestinationName());
            } else if (JMSChannelConfiguration.QUEUE.equals(cfg.getDestinationType())) {
                dest = session.createQueue(cfg.getDestinationName());
            } else {
                throw new BusFactoryException("Invalid configuration. Invalid JMS Destination type="+cfg.getDestinationType());
            }
            MessageProducer producer = session.createProducer(dest);
            if (publisher) {
                return new JMSPublisher(cnn, session, producer, dest, cfg.isSharedConnection(), mapperProvider);
            } else {
                return null;
            }
        } catch (JMSException e) {
            throw new BusFactoryException("Could not create JMS producer", e);
        }
    }

    public BusPublisher createPublisher(String configurationKey, String moduleName) throws BusFactoryException {
        JMSChannelConfiguration cfg;
        try {
            cfg = resolvePublisherConfiguration(configurationKey, moduleName);
        } catch (ConfigurationNotFoundException e) {
            throw new BusFactoryException("Failed to create publisher", e);
        }
        return (BusPublisher) createMessageProducerImpl(cfg, true);
    }

    public BusListener createListener(String configurationKey, String moduleName) throws BusFactoryException {
        try {
            JMSChannelConfiguration cfg = resolveListenerConfiguration(configurationKey, moduleName);
            return (BusListener) createMessageListenerImpl(cfg, true);
        } catch (ConfigurationNotFoundException e) {
           throw new BusFactoryException("Failed to create listener", e);
        }
    }

    public BusPollListener createPollListener(String configurationKey, String moduleName) {
        // TODO Auto-generated method stub
        return null;
    }

    private JMSChannelConfiguration resolvePublisherConfiguration(String configurationKey, String moduleName) throws ConfigurationNotFoundException {
        return configuration.getChannelForConnector("publisher", configurationKey, moduleName);
    }

    private JMSChannelConfiguration resolveListenerConfiguration(String configurationKey, String moduleName) throws ConfigurationNotFoundException {
        return configuration.getChannelForConnector("listener", configurationKey, moduleName);
    }

    public BusRequestPublisher createRequestPublisher(String configurationKey, String moduleName) throws BusFactoryException {
        BusListener listener = createListener(configurationKey, moduleName);
        BusPublisher publisher = createPublisher(configurationKey, moduleName);
        try {
            return new BusRequestPublisherImpl(publisher, listener);
        } catch (BusException e) {
            throw new BusFactoryException("Could not create Request Publisher", e);
        }
    }
    
    public BusRequestListener createRequestListener(String configurationKey, String moduleName) throws BusFactoryException {
        BusListener listener = createListener(configurationKey, moduleName);
        BusPublisher publisher = createPublisher(configurationKey, moduleName);
        try {
            return new BusRequestListenerImpl(listener, publisher);
        } catch (BusException e) {
            throw new BusFactoryException("Could not create Request Listener", e);
        }
    }

    public void release() {
        synchronized (connections) {
            for (JMSConnection cnn : connections.values()) {
                try {
                    cnn.close();
                } catch (Exception e) { /*Continue with the rest of them*/ }
            }
            connections.clear();
        }
        synchronized (factories) {
            factories.clear();
        }
    }
}
